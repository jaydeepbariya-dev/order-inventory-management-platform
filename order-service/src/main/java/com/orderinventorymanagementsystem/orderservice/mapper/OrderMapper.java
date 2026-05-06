package com.orderinventorymanagementsystem.orderservice.mapper;

import com.orderinventorymanagementsystem.orderservice.dto.*;
import com.orderinventorymanagementsystem.orderservice.entity.*;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Order order, List<OrderItem> items) {

        OrderResponseDTO dto = new OrderResponseDTO();
        
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setPaymentStatus(order.getPaymentStatus().name());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponseDTO> itemDTOs = items.stream().map(item -> {
            OrderItemResponseDTO i = new OrderItemResponseDTO();
            i.setProductId(item.getProductId());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPrice());
            return i;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);

        return dto;
    }
}