package com.orderinventorymanagementsystem.productservice.service;

import java.util.List;
import java.util.UUID;

import com.orderinventorymanagementsystem.productservice.dto.PageResponseDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductFilterRequestDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductRequestDTO;
import com.orderinventorymanagementsystem.productservice.dto.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto, UUID sellerId, UUID tenantId, String role);

    ProductResponseDTO getProductById(UUID productId, UUID tenantId);

    List<ProductResponseDTO> getAllProducts(UUID tenantId);

    ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO dto, UUID sellerId, UUID tenantId, String role);

    void deleteProduct(UUID productId, UUID sellerId, UUID tenantId, String role);

    List<ProductResponseDTO> searchProducts(String keyword, UUID tenantId);

    PageResponseDTO<ProductResponseDTO> getProductsByFilter(ProductFilterRequestDTO filter, UUID tenantId);
}