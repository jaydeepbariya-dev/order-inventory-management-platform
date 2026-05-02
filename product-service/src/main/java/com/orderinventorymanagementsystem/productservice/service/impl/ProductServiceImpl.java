package com.orderinventorymanagementsystem.productservice.service.impl;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.repository.ProductRepository;
import com.orderinventorymanagementsystem.productservice.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto, UUID sellerId, UUID tenantId) {
        return null;
    }

    @Override
    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO dto, UUID sellerId, UUID tenantId) {
        return null;
    }

    @Override
    public ProductResponseDTO getProduct(UUID productId, UUID tenantId) {
        return null;
    }

    @Override
    public List<ProductResponseDTO> getAllProducts(UUID tenantId) {
        return null;
    }

    @Override
    public void deleteProduct(UUID productId, UUID sellerId, UUID tenantId) {
    }

    @Override
    public PageResponseDTO<ProductResponseDTO> getProductsByFilter(ProductFilterRequestDTO filter, UUID tenantId) {
        return null;
    }
}