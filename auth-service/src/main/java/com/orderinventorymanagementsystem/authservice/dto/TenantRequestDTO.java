package com.orderinventorymanagementsystem.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class TenantRequestDTO {
    @Schema(description = "Tenant name", example = "Acme Corp")
    private String name;
    @Schema(description = "Tenant admin email", example = "admin@acme.com")
    private String email;
    @Schema(description = "Tenant password", example = "TenantP@ss123")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}