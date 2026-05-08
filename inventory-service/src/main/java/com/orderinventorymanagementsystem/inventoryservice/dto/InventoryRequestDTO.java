package com.orderinventorymanagementsystem.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class InventoryRequestDTO {

    @Schema(description = "Product identifier for inventory operations", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull
    private UUID productId;

    @Schema(description = "Quantity to reserve or deduct", example = "2")
    @NotNull
    @Min(1)
    private Integer quantity;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}