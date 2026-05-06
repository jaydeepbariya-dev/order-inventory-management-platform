package com.orderinventorymanagementsystem.inventoryservice.dto;

import java.util.UUID;

public class InventoryResponseDTO {

    private UUID productId;
    private Integer availableQty;
    private Integer reservedQty;

    public InventoryResponseDTO(UUID productId, Integer availableQty, Integer reservedQty) {
        this.productId = productId;
        this.availableQty = availableQty;
        this.reservedQty = reservedQty;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Integer availableQty) {
        this.availableQty = availableQty;
    }

    public Integer getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Integer reservedQty) {
        this.reservedQty = reservedQty;
    }

}