package com.orderinventorymanagementsystem.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PaymentRequestDTO {

    @NotNull
    private UUID orderId;

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