package com.orderinventorymanagementsystem.orderservice.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class OrderRequestDTO {

    @NotEmpty
    private List<OrderItemRequestDTO> items;

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

}