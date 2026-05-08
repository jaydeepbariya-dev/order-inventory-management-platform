package com.orderinventorymanagementsystem.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;


public class ProductResponseDTO implements Serializable{

    @Schema(description = "Unique product identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Product name", example = "Cool Watch")
    private String name;

    @Schema(description = "Product description", example = "A comfortable leather watch with water resistance")
    private String description;

    @Schema(description = "Product price", example = "199.99")
    private Double price;

    @Schema(description = "Stock status of the product", example = "IN_STOCK")
    private String stockStatus;

    public ProductResponseDTO() {
    }

    public ProductResponseDTO(UUID id, String name, String description,
            Double price, String stockStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockStatus = stockStatus;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

}