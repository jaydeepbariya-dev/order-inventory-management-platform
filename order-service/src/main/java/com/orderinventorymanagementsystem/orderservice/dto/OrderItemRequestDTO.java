package com.orderinventorymanagementsystem.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class OrderItemRequestDTO {

    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull
    private UUID productId;

    @Schema(example = "2")
    @NotNull
    private Integer quantity;

    @Schema(example = "499.99")
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