package com.orderinventorymanagementsystem.orderservice.service;

import com.orderinventorymanagementsystem.orderservice.dto.*;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponseDTO placeOrder(OrderRequestDTO dto, UUID userId, String idempotencyKey);

    OrderResponseDTO getOrder(UUID orderId, UUID userId);

    List<OrderResponseDTO> getOrdersByUserId(UUID userId);
}
