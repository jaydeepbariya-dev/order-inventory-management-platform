package com.orderinventorymanagementsystem.productservice.service;

import com.orderinventorymanagementsystem.productservice.dto.*;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO dto, UUID sellerId, UUID tenantId);

    ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO dto, UUID sellerId, UUID tenantId);

    ProductResponseDTO getProduct(UUID productId, UUID tenantId);

    List<ProductResponseDTO> getAllProducts(UUID tenantId);

    void deleteProduct(UUID productId, UUID sellerId, UUID tenantId);

    PageResponseDTO<ProductResponseDTO> getProductsByFilter(ProductFilterRequestDTO filter, UUID tenantId);
}