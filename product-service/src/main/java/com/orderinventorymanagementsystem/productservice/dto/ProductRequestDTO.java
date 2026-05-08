package com.orderinventorymanagementsystem.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class ProductRequestDTO {

    @Schema(description = "Name of the product", example = "Cool Watch")
    @NotBlank(message = "Product name is required")
    @Size(max = 100)
    private String name;

    @Schema(description = "Description of the product", example = "A comfortable leather watch with water resistance")
    @Size(max = 1000)
    private String description;

    @Schema(description = "Price of the product", example = "199.99")
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than 0")
    private Double price;

    @Schema(description = "Stock status of the product", example = "IN_STOCK")
    @NotNull(message = "Stock status is required")
    private String stockStatus;

    @Schema(description = "Available quantity of the product", example = "12")
    @Min(value = 1)
    private Integer quantity;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}