package com.orderinventorymanagementsystem.orderservice.entity;

import com.orderinventorymanagementsystem.orderservice.enums.OrderStatus;
import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders", 
    indexes = {
        @Index(name = "idx_order_user", columnList = "user_id"),
        @Index(name = "idx_order_status", columnList = "status")
    }
)
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Instant createdAt = Instant.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}