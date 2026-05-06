package com.orderinventorymanagementsystem.paymentservice.service.impl;

import com.orderinventorymanagementsystem.paymentservice.dto.*;
import com.orderinventorymanagementsystem.paymentservice.entity.Payment;
import com.orderinventorymanagementsystem.paymentservice.enums.PaymentStatus;
import com.orderinventorymanagementsystem.paymentservice.exception.*;
import com.orderinventorymanagementsystem.paymentservice.mapper.PaymentMapper;
import com.orderinventorymanagementsystem.paymentservice.repository.PaymentRepository;
import com.orderinventorymanagementsystem.paymentservice.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {

        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new InvalidPaymentAmountException("Payment amount must be greater than zero. Received: " + request.getAmount());
        }

        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING);

        // MOCK LOGIC (simulate real payment)
        boolean success = Math.random() > 0.2; // 80% success

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment saved = paymentRepository.save(payment);

        return PaymentMapper.toDTO(saved);
    }
}