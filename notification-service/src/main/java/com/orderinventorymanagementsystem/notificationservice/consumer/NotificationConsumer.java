package com.orderinventorymanagementsystem.notificationservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.orderinventorymanagementsystem.notificationservice.event.OrderCreatedEvent;
import com.orderinventorymanagementsystem.notificationservice.event.PaymentFailedEvent;
import com.orderinventorymanagementsystem.notificationservice.event.PaymentSuccessEvent;



@Service
public class NotificationConsumer {

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "notification-group")
    public void orderCreated(OrderCreatedEvent event) {

        System.out.println(
                "Send notification -> Order placed");
    }

    @KafkaListener(
            topics = "payment-success-topic",
            groupId = "notification-group")
    public void paymentSuccess(PaymentSuccessEvent event) {

        System.out.println(
                "Send notification -> Payment success");
    }

    @KafkaListener(
            topics = "payment-failed-topic",
            groupId = "notification-group")
    public void paymentFailed(PaymentFailedEvent event) {

        System.out.println(
                "Send notification -> Payment failed");
    }
}