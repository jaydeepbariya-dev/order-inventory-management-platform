package com.orderinventorymanagementsystem.productservice.controller;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.service.ProductService;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestHeader UUID tenantId) {

        return ResponseEntity.ok(productService.getAllProducts(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(
            @PathVariable UUID id,
            @RequestHeader UUID tenantId) {

        return ResponseEntity.ok(productService.getProduct(id, tenantId));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestBody ProductRequestDTO dto,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId) {

        return ResponseEntity.ok(productService.createProduct(dto, sellerId, tenantId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequestDTO dto,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId) {

        return ResponseEntity.ok(productService.updateProduct(id, dto, sellerId, tenantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID id,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId) {

        productService.deleteProduct(id, sellerId, tenantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> searchProducts(
            @ModelAttribute ProductFilterRequestDTO filter,
            @RequestHeader UUID tenantId) {

        return ResponseEntity.ok(
                productService.getProductsByFilter(filter, tenantId));
    }
}