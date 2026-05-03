package com.orderinventorymanagementsystem.productservice.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.orderinventorymanagementsystem.productservice.dto.PageResponseDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductFilterRequestDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductRequestDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductResponseDTO;
import com.orderinventorymanagementsystem.productservice.entity.Product;
import com.orderinventorymanagementsystem.productservice.enums.StockStatus;
import com.orderinventorymanagementsystem.productservice.repository.ProductRepository;
import com.orderinventorymanagementsystem.productservice.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto, UUID sellerId, UUID tenantId, String role) {

        if (!"SELLER".equals(role)) {
            throw new RuntimeException("Only SELLER can create product");
        }

        // check if product already exists with same name

        Product product = new Product();
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockStatus(StockStatus.valueOf(dto.getStockStatus()));
        product.setSellerId(sellerId);
        product.setTenantId(tenantId);

        Product saved = productRepository.save(product);

        return map(saved);
    }

    @Override
    public ProductResponseDTO getProductById(UUID productId, UUID tenantId) {

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return map(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts(UUID tenantId) {

        return productRepository.findByTenantId(tenantId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO dto,
            UUID sellerId, UUID tenantId, String role) {

        if (!"SELLER".equals(role)) {
            throw new RuntimeException("Only SELLER can update product");
        }

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized: not owner");
        }

        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockStatus(StockStatus.valueOf(dto.getStockStatus()));
        product.setUpdatedAt(Instant.now());

        return map(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID productId, UUID sellerId, UUID tenantId, String role) {

        if (!"SELLER".equals(role)) {
            throw new RuntimeException("Only SELLER can delete product");
        }

        Product product = productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized");
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

            // name (LIKE)
            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicates.add(cb.like(
                        cb.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"));
            }

            // minPrice
            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"),
                        filter.getMinPrice()));
            }

            // maxPrice
            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"),
                        filter.getMaxPrice()));
            }

            // stockStatus
            if (filter.getStockStatus() != null) {
                predicates.add(cb.equal(
                        root.get("stockStatus"),
                        filter.getStockStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 3. Execute query
        Page<Product> pageResult = productRepository.findAll(spec, pageable);

        // 4. Map to DTO
        List<ProductResponseDTO> content = pageResult.getContent()
                .stream()
                .map(this::map)
                .toList();

        // 5. Wrap response
        return new PageResponseDTO<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages());
    }
}