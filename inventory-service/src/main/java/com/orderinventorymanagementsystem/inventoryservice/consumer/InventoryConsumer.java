package com.orderinventorymanagementsystem.inventoryservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderinventorymanagementsystem.inventoryservice.event.PaymentFailedEvent;
import com.orderinventorymanagementsystem.inventoryservice.event.PaymentSuccessEvent;

@Service
public class InventoryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryConsumer.class);

    @KafkaListener(topics = "payment-success-topic", groupId = "inventory-group")
    public void consumePaymentSuccess(
            PaymentSuccessEvent event) {

        logger.info("Deducting inventory permanently for order: {}", event.getOrderId());
        // reservedQty -> permanently deducted
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "inventory-group")
    public void consumePaymentFailed(
            PaymentFailedEvent event) {

        logger.info("Releasing reserved inventory for order: {}", event.getOrderId());
        // reservedQty -> availableQty
    }
}