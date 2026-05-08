package com.orderinventorymanagementsystem.inventoryservice.controller;

import com.orderinventorymanagementsystem.inventoryservice.dto.*;
import com.orderinventorymanagementsystem.inventoryservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory Controller", description = "Inventory Management APIs")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Initialize Inventory", description = "Creates an inventory record for the given product")
    @PostMapping
    public ResponseEntity<String> initInventory(
        @RequestBody InventoryRequestDTO request) {
        String res = inventoryService.initInventory(request);
    return new ResponseEntity<>(res, HttpStatus.CREATED);
}

    @Operation(summary = "Check Stock", description = "Checks available stock for a product")
    @PostMapping("/check")
    public ResponseEntity<InventoryResponseDTO> checkStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.checkStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Reserve Stock", description = "Reserves stock for an order item")
    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponseDTO> reserveStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.reserveStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Release Stock", description = "Releases reserved stock back to inventory")
    @PostMapping("/release")
    public ResponseEntity<InventoryResponseDTO> releaseStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.releaseStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Deduct Stock", description = "Deducts reserved stock when an order is confirmed")
    @PostMapping("/deduct")
    public ResponseEntity<InventoryResponseDTO> deductStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.deductStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}