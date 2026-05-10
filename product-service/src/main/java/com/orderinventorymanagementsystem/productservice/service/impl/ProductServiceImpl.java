package com.orderinventorymanagementsystem.productservice.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import io.github.resilience4j.retry.Retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderinventorymanagementsystem.productservice.dto.PageResponseDTO;
import com.orderinventorymanagementsystem.productservice.exception.*;
import com.orderinventorymanagementsystem.productservice.dto.ProductFilterRequestDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductRequestDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductResponseDTO;
import com.orderinventorymanagementsystem.productservice.dto.client.InventoryRequestDTO;
import com.orderinventorymanagementsystem.productservice.entity.Product;
import com.orderinventorymanagementsystem.productservice.enums.StockStatus;
import com.orderinventorymanagementsystem.productservice.repository.ProductRepository;
import com.orderinventorymanagementsystem.productservice.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final Retry inventoryServiceRetry;

    public ProductServiceImpl(ProductRepository productRepository, RestTemplate restTemplate, Retry inventoryServiceRetry) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
        this.inventoryServiceRetry = inventoryServiceRetry;
    }

    @Override
    @CacheEvict(value = {
            "productById",
            "allProducts",
            "searchProducts",
            "filteredProducts"
    }, allEntries = true)
    public ProductResponseDTO createProduct(ProductRequestDTO dto, UUID sellerId, UUID tenantId, String role) {

        logger.info("Creating product for seller: {}, tenant: {}, role: {}", sellerId, tenantId, role);

        if (!"SELLER".equals(role)) {
            logger.warn("Unauthorized product creation attempt by user with role: {}", role);
            throw new UnauthorizedException("Access denied: Only SELLER role can create products");
        }

        productRepository.findByName(dto.getName())
                .ifPresent(p -> {
                    logger.warn("Product creation failed - product with name '{}' already exists", dto.getName());
                    throw new ProductAlreadyExistsException("Product with name '" + dto.getName() + "' already exists");
                });

        Product product = new Product();
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockStatus(StockStatus.valueOf(dto.getStockStatus()));
        product.setSellerId(sellerId);
        product.setTenantId(tenantId);

        Product saved = productRepository.save(product);
        logger.info("Product created with ID: {} for seller: {}", saved.getId(), sellerId);

        InventoryRequestDTO request = new InventoryRequestDTO();
        request.setProductId(saved.getId());
        request.setQuantity(dto.getQuantity());

        try {
            logger.debug("Initializing inventory for product: {}, quantity: {}", saved.getId(), dto.getQuantity());
            String res = Retry.decorateSupplier(inventoryServiceRetry, () ->
                restTemplate.postForObject("http://inventory-service:8084/api/v1/inventory", request, String.class)
            ).get();
            logger.debug("Inventory initialization response: {}", res);
        } catch (RestClientException ex) {
            logger.error("Failed to initialize inventory for product: {}, error: {}", saved.getId(), ex.getMessage(), ex);
            throw new InventoryServiceException("Failed to initialize inventory for product: " + ex.getMessage());
        }

        logger.info("Product creation completed successfully for product: {}", saved.getId());
        return map(saved);
    }

    @Override
    @Cacheable(value = "productById", key = "#productId + '-' + #tenantId")
    public ProductResponseDTO getProductById(UUID productId, UUID tenantId) {

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID '" + productId + "' not found"));

        return map(product);
    }

    @Override
    @Cacheable(value = "allProducts", key = "#tenantId")
    public List<ProductResponseDTO> getAllProducts(UUID tenantId) {

        return productRepository.findByTenantId(tenantId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @CacheEvict(value = {
            "productById",
            "allProducts",
            "searchProducts",
            "filteredProducts"
    }, allEntries = true)
    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO dto,
            UUID sellerId, UUID tenantId, String role) {

        if (!"SELLER".equals(role)) {
            throw new UnauthorizedException("Access denied: Only SELLER role can update products");
        }

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID '" + productId + "' not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("Access denied: You are not the owner of this product");
        }

        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockStatus(StockStatus.valueOf(dto.getStockStatus()));
        product.setUpdatedAt(Instant.now());

        return map(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = {
            "productById",
            "allProducts",
            "searchProducts",
            "filteredProducts"
    }, allEntries = true)
    public void deleteProduct(UUID productId, UUID sellerId, UUID tenantId, String role) {

        if (!"SELLER".equals(role)) {
            throw new UnauthorizedException("Access denied: Only SELLER role can delete products");
        }

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID '" + productId + "' not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new UnauthorizedException("Access denied: You are not the owner of this product");
        }

        productRepository.delete(product);
    }

    private ProductResponseDTO map(Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockStatus().toString());
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String keyword, UUID tenantId) {

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuntimeException("Search keyword cannot be empty");
        }

        List<Product> products = productRepository.findByTenantIdAndNameContainingIgnoreCase(
                tenantId,
                keyword.trim());

        return products.stream()
                .map(this::map)
                .toList();
    }

    @Override
    public PageResponseDTO<ProductResponseDTO> getProductsByFilter(
            ProductFilterRequestDTO filter,
            UUID tenantId) {

        Sort sort = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(filter.getSortBy()).ascending()
                : Sort.by(filter.getSortBy()).descending();

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                sort);

        Specification<Product> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("tenantId"), tenantId));

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"),
                        filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"),
                        filter.getMaxPrice()));
            }

            if (filter.getStockStatus() != null) {
                predicates.add(cb.equal(
                        root.get("stockStatus"),
                        filter.getStockStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> pageResult = productRepository.findAll(spec, pageable);

        List<ProductResponseDTO> content = pageResult.getContent()
                .stream()
                .map(this::map)
                .toList();

        return new PageResponseDTO<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }
}