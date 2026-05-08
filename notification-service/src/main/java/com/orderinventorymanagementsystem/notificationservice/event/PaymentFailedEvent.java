package com.orderinventorymanagementsystem.notificationservice.event;

import java.util.UUID;

public class PaymentFailedEvent {

    private UUID orderId;

    public PaymentFailedEvent() {
    }

    public PaymentFailedEvent(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}