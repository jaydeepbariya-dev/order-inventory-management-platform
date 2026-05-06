package com.orderinventorymanagementsystem.orderservice.service.impl;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.dto.client.InventoryRequestDTO;
import com.orderinventorymanagementsystem.orderservice.entity.Order;
import com.orderinventorymanagementsystem.orderservice.entity.OrderItem;
import com.orderinventorymanagementsystem.orderservice.enums.OrderStatus;
import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;
import com.orderinventorymanagementsystem.orderservice.repository.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
            throw new RuntimeException("Order items cannot be empty");
        }

        double totalAmount = 0;

        for (OrderItemRequestDTO item : dto.getItems()) {

            String url = "http://inventory-service:8083/api/v1/inventory/reserve";

            InventoryRequestDTO request = new InventoryRequestDTO();
            request.setProductId(item.getProductId());
            request.setQuantity(item.getQuantity());

            try {
                String res = restTemplate.postForObject(url, request, String.class);
                System.out.println(res);
            } catch (Exception ex) {
                Order failedOrder = new Order();
                failedOrder.setUserId(userId);
                failedOrder.setStatus(OrderStatus.FAILED);
                failedOrder.setTotalAmount(0.0);

                orderRepository.save(failedOrder);

                throw new RuntimeException("Stock reservation failed for product: " + item.getProductId());
            }

            totalAmount += item.getPrice() * item.getQuantity();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.RESERVED);
        order.setPaymentStatus(PaymentStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

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
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
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
