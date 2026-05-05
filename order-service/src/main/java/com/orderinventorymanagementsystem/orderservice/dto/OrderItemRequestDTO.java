package com.orderinventorymanagementsystem.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class OrderItemRequestDTO {

    @NotNull
    private UUID productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double price;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}