package com.orderinventorymanagementsystem.orderservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class OrderRequestDTO {

    @Schema(description = "List of items included in the order")
    @NotEmpty
    private List<OrderItemRequestDTO> items;

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

}