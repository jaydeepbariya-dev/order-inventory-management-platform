package com.orderinventorymanagementsystem.productservice.controller;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestHeader UUID tenantId) {

        List<ProductResponseDTO> res = productService.getAllProducts(tenantId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // GET PRODUCT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable UUID id,
            @RequestHeader UUID tenantId) {

        ProductResponseDTO res = productService.getProductById(id, tenantId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestBody @Valid ProductRequestDTO dto,
            @RequestHeader UUID sellerId,   
            @RequestHeader UUID tenantId,
            @RequestHeader String role) {

        ProductResponseDTO res = productService.createProduct(dto, sellerId, tenantId, role);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    // UPDATE PRODUCT
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable UUID id,
            @RequestBody @Valid ProductRequestDTO dto,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId,
            @RequestHeader String role) {

        ProductResponseDTO res = productService.updateProduct(id, dto, sellerId, tenantId, role);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // DELETE PRODUCT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable UUID id,
            @RequestHeader UUID sellerId,
            @RequestHeader UUID tenantId,
            @RequestHeader String role) {

        productService.deleteProduct(id, sellerId, tenantId, role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam String keyword,
            @RequestHeader UUID tenantId) {

        List<ProductResponseDTO> res = productService.searchProducts(keyword, tenantId);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> filterProducts(
            @ModelAttribute ProductFilterRequestDTO filter,
            @RequestHeader UUID tenantId) {

        PageResponseDTO<ProductResponseDTO> res = productService.getProductsByFilter(filter, tenantId);

        return new ResponseEntity<>(res, HttpStatus.OK);

    }
}