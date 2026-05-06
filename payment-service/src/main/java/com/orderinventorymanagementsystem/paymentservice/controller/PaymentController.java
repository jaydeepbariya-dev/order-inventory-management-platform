package com.orderinventorymanagementsystem.paymentservice.controller;

import com.orderinventorymanagementsystem.paymentservice.dto.*;
import com.orderinventorymanagementsystem.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> processPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO res = paymentService.processPayment(request);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}