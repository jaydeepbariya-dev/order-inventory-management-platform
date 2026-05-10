package com.orderinventorymanagementsystem.orderservice.impl;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.dto.client.PaymentResponseDTO;
import com.orderinventorymanagementsystem.orderservice.entity.Order;
import com.orderinventorymanagementsystem.orderservice.entity.OrderItem;
import com.orderinventorymanagementsystem.orderservice.enums.OrderStatus;
import com.orderinventorymanagementsystem.orderservice.enums.PaymentStatus;
import com.orderinventorymanagementsystem.orderservice.exception.InvalidOrderRequestException;
import com.orderinventorymanagementsystem.orderservice.exception.OrderNotFoundException;
import com.orderinventorymanagementsystem.orderservice.repository.OrderItemRepository;
import com.orderinventorymanagementsystem.orderservice.repository.OrderRepository;
import com.orderinventorymanagementsystem.orderservice.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.Retry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

        @Mock
        private OrderRepository orderRepository;

        @Mock
        private OrderItemRepository orderItemRepository;

        @Mock
        private RestTemplate restTemplate;

        @Mock
        private Retry inventoryServiceRetry;

        @Mock
        private RedisTemplate<String, Object> redisTemplate;

        @Mock
        private ValueOperations<String, Object> valueOperations;

        @InjectMocks
        private OrderServiceImpl orderService;

        private UUID userId;
        private UUID orderId;
        private UUID productId;

        private Order order;
        private String idempotencyKey;

        @BeforeEach
        void setUp() {

                userId = UUID.randomUUID();
                orderId = UUID.randomUUID();
                productId = UUID.randomUUID();
                idempotencyKey = "test-idempotency-key-" + UUID.randomUUID();

                order = new Order();
                order.setId(orderId);
                order.setUserId(userId);
                order.setStatus(OrderStatus.CONFIRMED);
                order.setPaymentStatus(PaymentStatus.SUCCESS);
                order.setTotalAmount(1000.0);

                when(redisTemplate.opsForValue()).thenReturn(valueOperations);
                when(valueOperations.get(anyString())).thenReturn(null);
        }

        @Test
        void placeOrder_success() {

                OrderRequestDTO request = new OrderRequestDTO();

                OrderItemRequestDTO item = new OrderItemRequestDTO();
                item.setProductId(productId);
                item.setQuantity(2);
                item.setPrice(500.0);

                request.setItems(List.of(item));

                when(orderRepository.save(any(Order.class)))
                                .thenReturn(order);

                PaymentResponseDTO paymentResponse = new PaymentResponseDTO();
                paymentResponse.setStatus(PaymentStatus.SUCCESS);

                when(restTemplate.postForObject(
                                contains("payments"),
                                any(),
                                eq(PaymentResponseDTO.class)))
                                .thenReturn(paymentResponse);

                OrderResponseDTO response = orderService.placeOrder(request, userId, idempotencyKey);

                assertNotNull(response);
                assertEquals("CONFIRMED", response.getStatus());
                assertEquals(PaymentStatus.SUCCESS.toString(), response.getPaymentStatus());

                verify(orderRepository, atLeastOnce()).save(any(Order.class));
                verify(valueOperations).set(contains("order:idempotency:"), any(), any());
        }

        @Test
        void placeOrder_shouldThrowException_whenItemsEmpty() {

                OrderRequestDTO request = new OrderRequestDTO();
                request.setItems(List.of());

                assertThrows(InvalidOrderRequestException.class, 
                        () -> orderService.placeOrder(request, userId, idempotencyKey));
        }

        @Test
        void getOrder_success() {

                when(orderRepository.findById(orderId))
                                .thenReturn(Optional.of(order));

                when(orderItemRepository.findByOrderId(orderId))
                                .thenReturn(List.of(new OrderItem()));

                OrderResponseDTO response = orderService.getOrder(orderId, userId);

                assertNotNull(response);
                assertEquals(orderId, response.getId());
        }

        @Test
        void getOrder_shouldThrowException_whenNotFound() {

                when(orderRepository.findById(orderId))
                                .thenReturn(Optional.empty());

                assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId, userId));
        }

        @Test
        void getOrdersByUserId_success() {

                when(orderRepository.findByUserId(userId))
                                .thenReturn(List.of(order));

                when(orderItemRepository.findByOrderId(orderId))
                                .thenReturn(List.of(new OrderItem()));

                List<OrderResponseDTO> response = orderService.getOrdersByUserId(userId);

                assertEquals(1, response.size());
        }
}