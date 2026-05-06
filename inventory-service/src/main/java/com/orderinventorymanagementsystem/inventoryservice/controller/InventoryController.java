package com.orderinventorymanagementsystem.inventoryservice.controller;

import com.orderinventorymanagementsystem.inventoryservice.dto.*;
import com.orderinventorymanagementsystem.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<String> initInventory(
        @RequestBody InventoryRequestDTO request) {
        String res = inventoryService.initInventory(request);
    return new ResponseEntity<>(res, HttpStatus.CREATED);
}

    @PostMapping("/check")
    public ResponseEntity<InventoryResponseDTO> checkStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.checkStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponseDTO> reserveStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.reserveStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/release")
    public ResponseEntity<InventoryResponseDTO> releaseStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.releaseStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/deduct")
    public ResponseEntity<InventoryResponseDTO> deductStock(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO res = inventoryService.deductStock(request);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}