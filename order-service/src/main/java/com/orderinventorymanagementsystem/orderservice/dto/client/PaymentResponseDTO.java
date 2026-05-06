package com.orderinventorymanagementsystem.orderservice.dto.client;


import java.util.UUID;

import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;

public class PaymentResponseDTO {

    private UUID paymentId;
    private UUID orderId;
    private Double amount;
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