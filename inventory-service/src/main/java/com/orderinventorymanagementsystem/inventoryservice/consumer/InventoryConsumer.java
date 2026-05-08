package com.orderinventorymanagementsystem.inventoryservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.orderinventorymanagementsystem.inventoryservice.event.PaymentFailedEvent;
import com.orderinventorymanagementsystem.inventoryservice.event.PaymentSuccessEvent;

@Service
public class InventoryConsumer {

    @KafkaListener(topics = "payment-success-topic", groupId = "inventory-group")
    public void consumePaymentSuccess(
            PaymentSuccessEvent event) {

        System.out.println(
                "Deduct inventory permanently for order: "
                        + event.getOrderId());

        // reservedQty -> permanently deducted
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "inventory-group")
    public void consumePaymentFailed(
            PaymentFailedEvent event) {

        System.out.println(
                "Release reserved inventory for order: "
                        + event.getOrderId());

        // reservedQty -> availableQty
    }
}