package com.orderinventorymanagementsystem.orderservice.controller;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestBody @Valid OrderRequestDTO dto,
            @RequestHeader UUID userId) {

        OrderResponseDTO res = orderService.placeOrder(dto, userId);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @PathVariable UUID id,
            @RequestHeader UUID userId) {

        OrderResponseDTO res = orderService.getOrder(id, userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getUserOrders(
            @RequestHeader UUID userId) {

        List<OrderResponseDTO> res = orderService.getUserOrders(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}