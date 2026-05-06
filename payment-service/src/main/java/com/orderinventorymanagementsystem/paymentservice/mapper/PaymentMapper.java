package com.orderinventorymanagementsystem.paymentservice.mapper;

import com.orderinventorymanagementsystem.paymentservice.dto.PaymentResponseDTO;
import com.orderinventorymanagementsystem.paymentservice.entity.Payment;

public class PaymentMapper {

    public static PaymentResponseDTO toDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}