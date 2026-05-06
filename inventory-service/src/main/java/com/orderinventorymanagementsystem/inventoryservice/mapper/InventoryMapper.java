package com.orderinventorymanagementsystem.inventoryservice.mapper;

import com.orderinventorymanagementsystem.inventoryservice.dto.InventoryResponseDTO;
import com.orderinventorymanagementsystem.inventoryservice.entity.Inventory;

public class InventoryMapper {

    public static InventoryResponseDTO toDTO(Inventory inventory) {
        return new InventoryResponseDTO(
                inventory.getProductId(),
                inventory.getAvailableQty(),
                inventory.getReservedQty());
    }
}