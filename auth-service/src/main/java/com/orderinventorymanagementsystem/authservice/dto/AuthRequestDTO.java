package com.orderinventorymanagementsystem.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthRequestDTO {
    @Schema(description = "User email for login", example = "user@example.com")
    private String email;
    @Schema(description = "User password for login", example = "P@ssw0rd123")
    private String password;

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