package com.orderinventorymanagementsystem.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderinventorymanagementsystem.notificationservice.event.OrderCreatedEvent;
import com.orderinventorymanagementsystem.notificationservice.event.PaymentFailedEvent;
import com.orderinventorymanagementsystem.notificationservice.event.PaymentSuccessEvent;



@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "notification-group")
    public void orderCreated(OrderCreatedEvent event) {

        logger.info("Sending notification for order placed: {}, amount: {}", event.getOrderId(), event.getTotalAmount());
    }

    @KafkaListener(
            topics = "payment-success-topic",
            groupId = "notification-group")
    public void paymentSuccess(PaymentSuccessEvent event) {

        logger.info("Sending notification for payment success: {}", event.getOrderId());
    }

    @KafkaListener(
            topics = "payment-failed-topic",
            groupId = "notification-group")
    public void paymentFailed(PaymentFailedEvent event) {

        logger.warn("Sending notification for payment failure: {}", event.getOrderId());
    }
}