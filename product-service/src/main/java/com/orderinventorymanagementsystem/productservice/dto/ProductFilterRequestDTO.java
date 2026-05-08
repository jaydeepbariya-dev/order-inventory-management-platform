package com.orderinventorymanagementsystem.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.orderinventorymanagementsystem.productservice.enums.StockStatus;

public class ProductFilterRequestDTO {

    @Schema(description = "Keyword match for product name", example = "watch")
    private String name;

    @Schema(description = "Minimum price filter", example = "50.0")
    private Double minPrice;

    @Schema(description = "Maximum price filter", example = "500.0")
    private Double maxPrice;

    @Schema(description = "Stock status filter", example = "IN_STOCK")
    private StockStatus stockStatus;

    @Schema(description = "Page number for pagination", example = "0")
    private Integer page = 0;

    @Schema(description = "Page size for pagination", example = "10")
    private Integer size = 10;

    @Schema(description = "Field to sort by", example = "createdAt")
    private String sortBy = "createdAt";

    @Schema(description = "Sort direction", example = "desc")
    private String sortDir = "desc";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public StockStatus getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(StockStatus stockStatus) {
        this.stockStatus = stockStatus;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

}