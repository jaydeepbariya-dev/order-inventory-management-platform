package com.orderinventorymanagementsystem.orderservice.controller;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order Controller", description = "Order Management APIs")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
            summary = "Place Order",
            description = "Creates a new order for user")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestBody @Valid OrderRequestDTO dto,
            @Parameter(description = "Authenticated user ID") @RequestHeader UUID userId,
            @Parameter(description = "Idempotency key for safe retries") @RequestHeader("Idempotency-Key") String idempotencyKey) {

        OrderResponseDTO res = orderService.placeOrder(dto, userId, idempotencyKey);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Order", description = "Retrieves an order by order ID")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @Parameter(description = "Order identifier") @PathVariable UUID id,
            @Parameter(description = "Authenticated user ID") @RequestHeader UUID userId) {

        OrderResponseDTO res = orderService.getOrder(id, userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "List Orders", description = "Returns all orders for the authenticated user")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(
            @Parameter(description = "Authenticated user ID") @RequestHeader UUID userId) {

        List<OrderResponseDTO> res = orderService.getOrdersByUserId(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}