package com.orderinventorymanagementsystem.inventoryservice.service;

import com.orderinventorymanagementsystem.inventoryservice.dto.InventoryRequestDTO;
import com.orderinventorymanagementsystem.inventoryservice.dto.InventoryResponseDTO;

public interface InventoryService {

    InventoryResponseDTO checkStock(InventoryRequestDTO request);

    InventoryResponseDTO reserveStock(InventoryRequestDTO request);

    InventoryResponseDTO releaseStock(InventoryRequestDTO request);

    InventoryResponseDTO deductStock(InventoryRequestDTO request);

    String initInventory(InventoryRequestDTO request);
}