package com.orderinventorymanagementsystem.orderservice.service.impl;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.dto.client.InventoryRequestDTO;
import com.orderinventorymanagementsystem.orderservice.entity.Order;
import com.orderinventorymanagementsystem.orderservice.entity.OrderItem;
import com.orderinventorymanagementsystem.orderservice.enums.OrderStatus;
import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;
import com.orderinventorymanagementsystem.orderservice.event.OrderCreatedEvent;
import com.orderinventorymanagementsystem.orderservice.exception.*;
import com.orderinventorymanagementsystem.orderservice.repository.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            RestTemplate restTemplate,
            RedisTemplate<String, Object> redisTemplate,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    @CacheEvict(value = { "orderById", "ordersByUser" }, allEntries = true)
    public OrderResponseDTO placeOrder(
            OrderRequestDTO dto,
            UUID userId,
            String idempotencyKey) {

        // ---------------------------------------------------
        // 1. CHECK IDEMPOTENCY
        // ---------------------------------------------------

        String redisKey = "order:idempotency:" + idempotencyKey;

        OrderResponseDTO cachedResponse = (OrderResponseDTO) redisTemplate
                .opsForValue()
                .get(redisKey);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        // ---------------------------------------------------
        // 2. VALIDATE REQUEST
        // ---------------------------------------------------

        if (dto.getItems() == null
                || dto.getItems().isEmpty()) {

            throw new InvalidOrderRequestException(
                    "Order must contain at least one item");
        }

        // ---------------------------------------------------
        // 3. CREATE ORDER
        // ---------------------------------------------------

        Order order = new Order();

        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        // ---------------------------------------------------
        // 4. RESERVE INVENTORY (SYNC REST)
        // ---------------------------------------------------

        double totalAmount = 0;

        List<InventoryRequestDTO> reservedItems = new ArrayList<>();

        try {

            for (OrderItemRequestDTO item : dto.getItems()) {

                InventoryRequestDTO invReq = new InventoryRequestDTO();

                invReq.setProductId(item.getProductId());
                invReq.setQuantity(item.getQuantity());

                // synchronous validation
                restTemplate.postForObject(
                        "http://inventory-service:8084/api/v1/inventory/reserve",
                        invReq,
                        Void.class);

                reservedItems.add(invReq);

                totalAmount += item.getPrice() * item.getQuantity();
            }

        } catch (Exception ex) {

            savedOrder.setStatus(OrderStatus.FAILED);

            orderRepository.save(savedOrder);

            throw new RuntimeException(
                    "Inventory reservation failed");
        }

        // ---------------------------------------------------
        // 5. SAVE RESERVED STATUS
        // ---------------------------------------------------

        savedOrder.setStatus(OrderStatus.RESERVED);
        savedOrder.setTotalAmount(totalAmount);

        orderRepository.save(savedOrder);

        // ---------------------------------------------------
        // 6. SAVE ORDER ITEMS
        // ---------------------------------------------------

        List<OrderItemResponseDTO> responseItems = new ArrayList<>();

        for (OrderItemRequestDTO item : dto.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());

            orderItemRepository.save(orderItem);

            OrderItemResponseDTO responseItem = new OrderItemResponseDTO();

            responseItem.setProductId(item.getProductId());
            responseItem.setQuantity(item.getQuantity());
            responseItem.setPrice(item.getPrice());

            responseItems.add(responseItem);
        }

        // ---------------------------------------------------
        // 7. PUBLISH EVENT TO KAFKA
        // ---------------------------------------------------

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getUserId(),
                totalAmount);

        kafkaTemplate.send(
                "order-created-topic",
                savedOrder.getId().toString(),
                event);

        // ---------------------------------------------------
        // 8. BUILD RESPONSE
        // ---------------------------------------------------

        OrderResponseDTO response = new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus().toString(),
                savedOrder.getPaymentStatus().toString(),
                savedOrder.getCreatedAt());

        response.setItems(responseItems);

        // ---------------------------------------------------
        // 9. STORE IDEMPOTENT RESPONSE
        // ---------------------------------------------------

        redisTemplate.opsForValue().set(
                redisKey,
                response,
                Duration.ofMinutes(30));

        return response;
    }

    @Override
    @Cacheable(value = "orderById", key = "#orderId + '-' + #userId")
    public OrderResponseDTO getOrder(UUID orderId, UUID userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID '" + orderId + "' not found"));

        if (!order.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You do not have permission to access this order");
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        List<OrderItemResponseDTO> itemsResponse = orderItems.stream()
                .map(item -> {
                    OrderItemResponseDTO dto = new OrderItemResponseDTO();
                    dto.setProductId(item.getProductId());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    return dto;
                })
                .toList();

        OrderResponseDTO response = new OrderResponseDTO(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus().toString(),
                order.getPaymentStatus().toString(),
                order.getCreatedAt());
        response.setItems(itemsResponse);
        return response;
    }

    @Override
    @Cacheable(value = "ordersByUser", key = "#userId")
    public List<OrderResponseDTO> getOrdersByUserId(UUID userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
                    List<OrderItemResponseDTO> itemsResponse = orderItems.stream()
                            .map(item -> {
                                OrderItemResponseDTO dto = new OrderItemResponseDTO();
                                dto.setProductId(item.getProductId());
                                dto.setQuantity(item.getQuantity());
                                dto.setPrice(item.getPrice());
                                return dto;
                            })
                            .toList();

                    OrderResponseDTO response = new OrderResponseDTO(
                            order.getId(),
                            order.getUserId(),
                            order.getTotalAmount(),
                            order.getStatus().toString(),
                            order.getPaymentStatus().toString(),
                            order.getCreatedAt());
                    response.setItems(itemsResponse);
                    return response;
                })
                .toList();
    }
}
