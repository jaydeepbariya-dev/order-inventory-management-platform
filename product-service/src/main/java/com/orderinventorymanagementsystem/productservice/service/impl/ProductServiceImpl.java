package com.orderinventorymanagementsystem.productservice.service.impl;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.entity.Product;
import com.orderinventorymanagementsystem.productservice.repository.ProductRepository;
import com.orderinventorymanagementsystem.productservice.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto, UUID sellerId, UUID tenantId) {

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setSellerId(sellerId);
        product.setTenantId(tenantId);

        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Override
    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO dto, UUID sellerId, UUID tenantId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // seller + tenant validation (important)
        if (!product.getSellerId().equals(sellerId) || !product.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized");
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return mapToDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO getProduct(UUID productId, UUID tenantId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized");
        }

        return mapToDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts(UUID tenantId) {
        return productRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(UUID productId, UUID sellerId, UUID tenantId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId) || !product.getTenantId().equals(tenantId)) {
            throw new RuntimeException("Unauthorized");
        }

        productRepository.delete(product);
    }

    private ProductResponseDTO mapToDTO(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}