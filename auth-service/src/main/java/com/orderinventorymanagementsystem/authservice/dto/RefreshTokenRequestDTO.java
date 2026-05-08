package com.orderinventorymanagementsystem.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RefreshTokenRequestDTO {
    @Schema(description = "Refresh token for obtaining a new access token", example = "dGhpc2lzcmVmcmVzaHRva2VuMTIz")
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}