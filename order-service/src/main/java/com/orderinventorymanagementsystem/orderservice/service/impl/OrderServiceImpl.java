package com.orderinventorymanagementsystem.orderservice.service.impl;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.dto.client.InventoryRequestDTO;
import com.orderinventorymanagementsystem.orderservice.dto.client.NotificationRequestDTO;
import com.orderinventorymanagementsystem.orderservice.dto.client.NotificationResponseDTO;
import com.orderinventorymanagementsystem.orderservice.dto.client.PaymentRequestDTO;
import com.orderinventorymanagementsystem.orderservice.dto.client.PaymentResponseDTO;
import com.orderinventorymanagementsystem.orderservice.entity.Order;
import com.orderinventorymanagementsystem.orderservice.entity.OrderItem;
import com.orderinventorymanagementsystem.orderservice.enums.NotificationType;
import com.orderinventorymanagementsystem.orderservice.enums.OrderStatus;
import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;
import com.orderinventorymanagementsystem.orderservice.event.OrderCreatedEvent;
import com.orderinventorymanagementsystem.orderservice.exception.*;
import com.orderinventorymanagementsystem.orderservice.repository.*;
import com.orderinventorymanagementsystem.orderservice.service.OrderService;

import org.apache.kafka.clients.producer.KafkaProducer;
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
    private final RedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderServiceImpl(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository, RestTemplate restTemplate, RedisTemplate redisTemplate,
            KafkaTemplate kafkaTemplate) {
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

        // 1. Check Redis for existing response
        String redisKey = "order:idempotency:" + idempotencyKey;

        OrderResponseDTO cachedResponse = (OrderResponseDTO) redisTemplate.opsForValue().get(redisKey);

        if (cachedResponse != null) {
            return cachedResponse;
        }

        // 2. Validate request
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new InvalidOrderRequestException(
                    "Order must contain at least one item");
        }

        // 3. Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setTotalAmount(0.0);

        Order savedOrder = orderRepository.save(order);

        double totalAmount = 0;

        List<InventoryRequestDTO> reservedItems = new ArrayList<>();

        try {

            // 4. Reserve Inventory
            for (OrderItemRequestDTO item : dto.getItems()) {

                InventoryRequestDTO invReq = new InventoryRequestDTO();

                invReq.setProductId(item.getProductId());
                invReq.setQuantity(item.getQuantity());

                restTemplate.postForObject(
                        "http://inventory-service:8084/api/v1/inventory/reserve",
                        invReq,
                        Void.class);

                reservedItems.add(invReq);

                totalAmount += item.getPrice() * item.getQuantity();
            }

            savedOrder.setStatus(OrderStatus.RESERVED);
            orderRepository.save(savedOrder);

            OrderCreatedEvent event = new OrderCreatedEvent(
                    savedOrder.getId(),
                    savedOrder.getUserId(),
                    totalAmount);

            kafkaTemplate.send(
                    "order-created-topic",
                    savedOrder.getId().toString(),
                    event);

            // 5. Call Payment Service
            PaymentRequestDTO paymentRequest = new PaymentRequestDTO();

            paymentRequest.setOrderId(savedOrder.getId());
            paymentRequest.setAmount(totalAmount);

            PaymentResponseDTO paymentResponse = restTemplate.postForObject(
                    "http://payment-service:8085/api/v1/payments",
                    paymentRequest,
                    PaymentResponseDTO.class);

            if (paymentResponse == null
                    || paymentResponse.getStatus() == null) {

                throw new RuntimeException("Payment failed");
            }

            // 6. Handle Payment Success
            if (paymentResponse.getStatus() == PaymentStatus.SUCCESS) {

                for (InventoryRequestDTO item : reservedItems) {

                    restTemplate.postForObject(
                            "http://inventory-service:8084/api/v1/inventory/deduct",
                            item,
                            Void.class);
                }

                savedOrder.setStatus(OrderStatus.CONFIRMED);
                savedOrder.setPaymentStatus(
                        PaymentStatus.SUCCESS);

            } else {

                // 7. Payment Failure -> Release Inventory
                for (InventoryRequestDTO item : reservedItems) {

                    restTemplate.postForObject(
                            "http://inventory-service:8084/api/v1/inventory/release",
                            item,
                            Void.class);
                }

                savedOrder.setStatus(OrderStatus.FAILED);
                savedOrder.setPaymentStatus(
                        PaymentStatus.FAILED);
            }

        } catch (Exception ex) {

            // 8. Any failure -> Release Inventory
            for (InventoryRequestDTO item : reservedItems) {

                try {

                    restTemplate.postForObject(
                            "http://inventory-service:8084/api/v1/inventory/release",
                            item,
                            Void.class);

                } catch (Exception ignore) {
                    System.out.println("Inventory release failed");
                }
            }

            savedOrder.setStatus(OrderStatus.FAILED);
            savedOrder.setPaymentStatus(PaymentStatus.FAILED);
        }

        savedOrder.setTotalAmount(totalAmount);

        orderRepository.save(savedOrder);

        // 9. Save Order Items
        List<OrderItemResponseDTO> orderItemsResponse = new ArrayList<>();

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

            orderItemsResponse.add(responseItem);
        }

        // 10. Notification
        NotificationRequestDTO notificationRequestDTO = new NotificationRequestDTO();

        notificationRequestDTO.setMessage("Order Placed");
        notificationRequestDTO.setType(NotificationType.EMAIL);
        notificationRequestDTO.setUserId(userId);

        try {

            restTemplate.postForObject(
                    "http://notification-service:8086/api/v1/notifications",
                    notificationRequestDTO,
                    NotificationResponseDTO.class);

        } catch (Exception e) {
            System.out.println("Notification failed");
        }

        // 11. Build Response
        OrderResponseDTO response = new OrderResponseDTO(
                savedOrder.getId(),
                savedOrder.getUserId(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus().toString(),
                savedOrder.getPaymentStatus().toString(),
                savedOrder.getCreatedAt());

        response.setItems(orderItemsResponse);

        // 12. Store response in Redis
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
