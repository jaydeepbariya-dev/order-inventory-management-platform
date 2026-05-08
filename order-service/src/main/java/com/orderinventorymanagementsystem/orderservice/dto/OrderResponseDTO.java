package com.orderinventorymanagementsystem.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderResponseDTO implements Serializable{

    @Schema(description = "Order identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "User who placed the order", example = "99aadde0-96ad-4c86-8194-226673f806b9")
    private UUID userId;

    @Schema(description = "Total amount of the order", example = "1499.99")
    private Double totalAmount;

    @Schema(description = "Order status", example = "CONFIRMED")
    private String status;

    @Schema(description = "Payment status", example = "SUCCESS")
    private String paymentStatus;

    @Schema(description = "Order creation timestamp", example = "2026-05-08T14:35:00Z")
    private Instant createdAt;

    @Schema(description = "Ordered items")
    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO(UUID id, UUID userId, Double totalAmount, String status, String paymentStatus,
            Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public OrderResponseDTO() {
        super();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }

}
