package com.orderinventorymanagementsystem.productservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

public class PageResponseDTO<T> implements Serializable {

    @Schema(description = "Page content")
    private List<T> content;

    @Schema(description = "Current page number", example = "0")
    private int page;

    @Schema(description = "Number of items per page", example = "10")
    private int size;

    @Schema(description = "Total number of elements", example = "100")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;

    public PageResponseDTO() {
    }

    public PageResponseDTO(List<T> content, int page, int size,
            long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}