package com.orderinventorymanagementsystem.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public class TenantResponseDTO {
    @Schema(description = "Tenant identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "Tenant name", example = "Acme Corp")
    private String name;
    @Schema(description = "Tenant status", example = "ACTIVE")
    private String status;

    public TenantResponseDTO(UUID id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}