package com.orderinventorymanagementsystem.orderservice.service.impl;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.dto.client.InventoryRequestDTO;
import com.orderinventorymanagementsystem.orderservice.dto.client.PaymentRequestDTO;
import com.orderinventorymanagementsystem.orderservice.dto.client.PaymentResponseDTO;
import com.orderinventorymanagementsystem.orderservice.entity.Order;
import com.orderinventorymanagementsystem.orderservice.entity.OrderItem;
import com.orderinventorymanagementsystem.orderservice.enums.OrderStatus;
import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;
import com.orderinventorymanagementsystem.orderservice.exception.*;
import com.orderinventorymanagementsystem.orderservice.repository.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;

    public OrderServiceImpl(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public OrderResponseDTO placeOrder(OrderRequestDTO dto, UUID userId) {

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new InvalidOrderRequestException("Order must contain at least one item");
        }

        // 1. Create Order (CREATED)
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        double totalAmount = 0;

        List<InventoryRequestDTO> reservedItems = new ArrayList<>();

        try {
            // 2. Reserve Inventory
            for (OrderItemRequestDTO item : dto.getItems()) {

                InventoryRequestDTO invReq = new InventoryRequestDTO();
                invReq.setProductId(item.getProductId());
                invReq.setQuantity(item.getQuantity());

                restTemplate.postForObject(
                        "http://localhost:8084/api/v1/inventory/reserve",
                        invReq,
                        Void.class);

                reservedItems.add(invReq);

                totalAmount += item.getPrice() * item.getQuantity();
            }

            savedOrder.setStatus(OrderStatus.RESERVED);
            orderRepository.save(savedOrder);

            // 3. Call Payment Service
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
            paymentRequest.setOrderId(savedOrder.getId());
            paymentRequest.setAmount(totalAmount);

            PaymentResponseDTO paymentResponse = restTemplate.postForObject(
                    "http://payment-service:8084/api/v1/payments",
                    paymentRequest,
                    PaymentResponseDTO.class);

            // 4. Handle Payment Result

            if (paymentResponse == null || paymentResponse.getStatus() == null) {
                throw new RuntimeException("Payment failed");
            }

            if (paymentResponse.getStatus() == PaymentStatus.SUCCESS) {

                // Deduct stock
                for (InventoryRequestDTO item : reservedItems) {
                    restTemplate.postForObject(
                            "http://inventory-service:8084/api/v1/inventory/deduct",
                            item,
                            Void.class);
                }

                savedOrder.setStatus(OrderStatus.CONFIRMED);
                savedOrder.setPaymentStatus(PaymentStatus.SUCCESS);

            } else {

                // Release stock
                for (InventoryRequestDTO item : reservedItems) {
                    restTemplate.postForObject(
                            "http://inventory-service:8083/api/v1/inventory/release",
                            item,
                            Void.class);
                }

                savedOrder.setStatus(OrderStatus.FAILED);
                savedOrder.setPaymentStatus(PaymentStatus.FAILED);
            }

        } catch (Exception ex) {

            // If anything fails → release stock
            for (InventoryRequestDTO item : reservedItems) {
                try {
                    restTemplate.postForObject(
                            "http://inventory-service:8083/api/v1/inventory/release",
                            item,
                            Void.class);
                } catch (Exception ignore) {
                }
            }

            savedOrder.setStatus(OrderStatus.FAILED);
            savedOrder.setPaymentStatus(PaymentStatus.FAILED);
        }

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        // 5. Save Order Items
        for (OrderItemRequestDTO item : dto.getItems()) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());

            orderItemRepository.save(orderItem);
        }

        return new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus().toString(),
                savedOrder.getPaymentStatus().toString(),
                savedOrder.getCreatedAt());
    }

    @Override
    public OrderResponseDTO getOrder(UUID orderId, UUID userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID '" + orderId + "' not found"));

        if (!order.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have permission to access this order");
        }

        return new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().toString(),
                order.getPaymentStatus().toString(),
                order.getCreatedAt());
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(UUID userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(order -> new OrderResponseDTO(
                        order.getId(),
                        order.getUserId(),
                        order.getTotalAmount(),
                        order.getStatus().toString(),
                        order.getPaymentStatus().toString(), order.getCreatedAt()))
                .toList();
    }
}
