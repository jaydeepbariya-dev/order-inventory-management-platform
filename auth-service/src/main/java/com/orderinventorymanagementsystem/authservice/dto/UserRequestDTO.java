package com.orderinventorymanagementsystem.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

import com.orderinventorymanagementsystem.authservice.enums.Role;

public class UserRequestDTO {
    @Schema(description = "User full name", example = "John Doe")
    private String name;
    @Schema(description = "User email", example = "john.doe@example.com")
    private String email;
    @Schema(description = "User password", example = "UserP@ss123")
    private String password;
    @Schema(description = "User role", example = "SELLER")
    private Role role;
    @Schema(description = "Tenant ID the user belongs to", example = "edb1ab0d-dfe3-4d78-bbd3-fb019fb61a21")
    private UUID tenantId;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

}