package com.orderinventorymanagementsystem.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PaymentRequestDTO {

    @Schema(description = "Order identifier for payment", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull
    private UUID orderId;

    @Schema(description = "Payment amount", example = "1499.99")
    @NotNull
    private Double amount;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}