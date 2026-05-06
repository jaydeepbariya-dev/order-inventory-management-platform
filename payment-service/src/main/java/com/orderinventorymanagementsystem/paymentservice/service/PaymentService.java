package com.orderinventorymanagementsystem.paymentservice.service;

import com.orderinventorymanagementsystem.paymentservice.dto.*;

public interface PaymentService {

    PaymentResponseDTO processPayment(PaymentRequestDTO request);

}