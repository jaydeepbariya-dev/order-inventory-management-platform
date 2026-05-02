package com.orderinventorymanagementsystem.productservice.controller;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // NOTE: sellerId & tenantId will come from JWT later

    @PostMapping
    public ProductResponseDTO create(
            @RequestBody ProductRequestDTO dto,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId) {
        return productService.createProduct(dto, sellerId, tenantId);
    }

    @PutMapping("/{id}")
    public ProductResponseDTO update(
            @PathVariable UUID id,
            @RequestBody ProductRequestDTO dto,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId) {
        return productService.updateProduct(id, dto, sellerId, tenantId);
    }

    @GetMapping("/{id}")
    public ProductResponseDTO get(
            @PathVariable UUID id,
            @RequestHeader UUID tenantId) {
        return productService.getProduct(id, tenantId);
    }

    @GetMapping
    public List<ProductResponseDTO> getAll(
            @RequestHeader UUID tenantId) {
        return productService.getAllProducts(tenantId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable UUID id,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId) {
        productService.deleteProduct(id, sellerId, tenantId);
    }
}