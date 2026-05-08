package com.orderinventorymanagementsystem.productservice.controller;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Controller", description = "Product Management APIs")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET ALL PRODUCTS
    @Operation(summary = "Get All Products", description = "Returns all products for the tenant")
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId) {

        List<ProductResponseDTO> res = productService.getAllProducts(tenantId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // GET PRODUCT BY ID
    @Operation(summary = "Get Product By ID", description = "Returns a product by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Product identifier") @PathVariable UUID id,
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId) {

        ProductResponseDTO res = productService.getProductById(id, tenantId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Create Product", description = "Creates a new product for the tenant")
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestBody @Valid ProductRequestDTO dto,
            @Parameter(description = "Seller identifier") @RequestHeader UUID sellerId,   
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId,
            @Parameter(description = "User role header") @RequestHeader String role) {

        ProductResponseDTO res = productService.createProduct(dto, sellerId, tenantId, role);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    // UPDATE PRODUCT
    @Operation(summary = "Update Product", description = "Updates an existing product")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Product identifier") @PathVariable UUID id,
            @RequestBody @Valid ProductRequestDTO dto,
            @Parameter(description = "Seller identifier") @RequestHeader UUID sellerId,
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId,
            @Parameter(description = "User role header") @RequestHeader String role) {

        ProductResponseDTO res = productService.updateProduct(id, dto, sellerId, tenantId, role);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // DELETE PRODUCT
    @Operation(summary = "Delete Product", description = "Deletes a product by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product identifier") @PathVariable UUID id,
            @Parameter(description = "Seller identifier") @RequestHeader UUID sellerId,
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId,
            @Parameter(description = "User role header") @RequestHeader String role) {

        productService.deleteProduct(id, sellerId, tenantId, role);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Search Products", description = "Searches products by keyword")
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId) {

        List<ProductResponseDTO> res = productService.searchProducts(keyword, tenantId);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Filter Products", description = "Filters products by criteria")
    @GetMapping("/filter")
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> filterProducts(
            @Parameter(description = "Filter criteria") @ModelAttribute ProductFilterRequestDTO filter,
            @Parameter(description = "Tenant identifier") @RequestHeader UUID tenantId) {

        PageResponseDTO<ProductResponseDTO> res = productService.getProductsByFilter(filter, tenantId);

        return new ResponseEntity<>(res, HttpStatus.OK);

    }
}