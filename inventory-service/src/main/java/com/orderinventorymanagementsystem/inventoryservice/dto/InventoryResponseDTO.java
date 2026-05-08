package com.orderinventorymanagementsystem.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public class InventoryResponseDTO {

    @Schema(description = "Product identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;
    @Schema(description = "Available quantity", example = "25")
    private Integer availableQty;
    @Schema(description = "Reserved quantity", example = "5")
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