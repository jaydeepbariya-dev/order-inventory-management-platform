package com.orderinventorymanagementsystem.paymentservice.consumer;

import java.util.Random;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.orderinventorymanagementsystem.paymentservice.event.OrderCreatedEvent;
import com.orderinventorymanagementsystem.paymentservice.event.PaymentFailedEvent;
import com.orderinventorymanagementsystem.paymentservice.event.PaymentSuccessEvent;

@Service
public class PaymentConsumer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "payment-group")
    public void consumeOrderCreated(OrderCreatedEvent event) {

        System.out.println("Payment processing started for order: "
                + event.getOrderId());

        boolean paymentSuccess = new Random().nextBoolean();

        if (paymentSuccess) {

            PaymentSuccessEvent successEvent =
                    new PaymentSuccessEvent(event.getOrderId());

            kafkaTemplate.send(
                    "payment-success-topic",
                    event.getOrderId().toString(),
                    successEvent);

            System.out.println("Payment success");

        } else {

            PaymentFailedEvent failedEvent =
                    new PaymentFailedEvent(event.getOrderId());

            kafkaTemplate.send(
                    "payment-failed-topic",
                    event.getOrderId().toString(),
                    failedEvent);

            System.out.println("Payment failed");
        }
    }
}