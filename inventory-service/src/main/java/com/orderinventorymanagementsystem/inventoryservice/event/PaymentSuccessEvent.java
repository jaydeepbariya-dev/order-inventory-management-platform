package com.orderinventorymanagementsystem.inventoryservice.event;

import java.util.UUID;

public class PaymentSuccessEvent {

    private UUID orderId;

    public PaymentSuccessEvent() {
    }

    public PaymentSuccessEvent(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}
