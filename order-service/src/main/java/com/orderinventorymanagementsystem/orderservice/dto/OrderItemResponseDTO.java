package com.orderinventorymanagementsystem.orderservice.dto;

import java.io.Serializable;
import java.util.UUID;

public class OrderItemResponseDTO implements Serializable{

    private UUID productId;
    private Integer quantity;
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
