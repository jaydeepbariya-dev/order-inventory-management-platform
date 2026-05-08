package com.orderinventorymanagementsystem.productservice.dto;

import java.io.Serializable;
import java.util.UUID;


public class ProductResponseDTO implements Serializable{

    private UUID id;
    private String name;
    private String description;
    private Double price;
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