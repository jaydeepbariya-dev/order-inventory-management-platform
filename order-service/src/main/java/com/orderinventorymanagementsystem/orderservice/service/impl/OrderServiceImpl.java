package com.orderinventorymanagementsystem.orderservice.service.impl;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.repository.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO dto, UUID userId) {
        return null;
    }

    @Override
    public OrderResponseDTO getOrder(UUID orderId, UUID userId) {
        return null;
    }

    @Override
    public List<OrderResponseDTO> getUserOrders(UUID userId) {
        return null;
    }
}
