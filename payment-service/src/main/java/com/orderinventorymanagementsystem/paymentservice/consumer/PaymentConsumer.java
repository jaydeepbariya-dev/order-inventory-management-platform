package com.orderinventorymanagementsystem.paymentservice.consumer;

import java.util.Random;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderinventorymanagementsystem.paymentservice.event.OrderCreatedEvent;
import com.orderinventorymanagementsystem.paymentservice.event.PaymentFailedEvent;
import com.orderinventorymanagementsystem.paymentservice.event.PaymentSuccessEvent;

@Service
public class PaymentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentConsumer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "payment-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {

        logger.info("Payment processing started for order: {}, amount: {}", event.getOrderId(), event.getTotalAmount());

        boolean paymentSuccess = new Random().nextBoolean();

        if (paymentSuccess) {

            PaymentSuccessEvent successEvent =
                    new PaymentSuccessEvent(event.getOrderId());

            kafkaTemplate.send(
                    "payment-success-topic",
                    event.getOrderId().toString(),
                    successEvent);

            logger.info("Payment succeeded for order: {}", event.getOrderId());

        } else {

            PaymentFailedEvent failedEvent =
                    new PaymentFailedEvent(event.getOrderId());

            kafkaTemplate.send(
                    "payment-failed-topic",
                    event.getOrderId().toString(),
                    failedEvent);

            logger.warn("Payment failed for order: {}", event.getOrderId());
        }
    }
}