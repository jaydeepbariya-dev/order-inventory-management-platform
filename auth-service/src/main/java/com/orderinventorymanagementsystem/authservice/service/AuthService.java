package com.orderinventorymanagementsystem.authservice.service;

import com.orderinventorymanagementsystem.authservice.dto.AuthRequestDTO;
import com.orderinventorymanagementsystem.authservice.dto.AuthResponse;
import com.orderinventorymanagementsystem.authservice.dto.RefreshTokenRequestDTO;
import com.orderinventorymanagementsystem.authservice.dto.TenantRequestDTO;
import com.orderinventorymanagementsystem.authservice.dto.TenantResponseDTO;
import com.orderinventorymanagementsystem.authservice.dto.UserRequestDTO;

public interface AuthService {
    public TenantResponseDTO registerTenant(TenantRequestDTO tenantRequestDTO);

    public AuthResponse registerUser(UserRequestDTO userRequestDTO);

    public AuthResponse loginUser(AuthRequestDTO authRequestDTO);

    public AuthResponse refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO);

    public String logout(String token);
}
