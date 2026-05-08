package com.orderinventorymanagementsystem.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;

public class OrderItemResponseDTO implements Serializable{

    @Schema(description = "Product identifier in the order item", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID productId;

    @Schema(description = "Quantity ordered for this product", example = "2")
    private Integer quantity;

    @Schema(description = "Price per unit for this product", example = "499.99")
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
