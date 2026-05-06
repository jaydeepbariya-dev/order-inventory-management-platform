package com.orderinventorymanagementsystem.productservice.dto.client;

import java.util.UUID;

public class InventoryRequestDTO {

    private UUID productId;
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