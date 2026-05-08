package com.orderinventorymanagementsystem.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.orderinventorymanagementsystem.paymentservice.enums.PaymentStatus;

import java.util.UUID;

public class PaymentResponseDTO {

    @Schema(description = "Payment identifier", example = "660e8400-e29b-41d4-a716-446655440000")
    private UUID paymentId;
    @Schema(description = "Order identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID orderId;
    @Schema(description = "Payment amount", example = "1499.99")
    private Double amount;
    @Schema(description = "Payment status", example = "SUCCESS")
    private PaymentStatus status;

    public PaymentResponseDTO(UUID paymentId, UUID orderId, Double amount, PaymentStatus status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

}