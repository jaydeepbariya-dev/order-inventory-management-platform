package com.orderinventorymanagementsystem.authservice.service.impl;

import com.orderinventorymanagementsystem.authservice.dto.*;
import com.orderinventorymanagementsystem.authservice.repository.*;
import com.orderinventorymanagementsystem.authservice.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public AuthServiceImpl(UserRepository userRepository,
            TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public TenantResponseDTO registerTenant(TenantRequestDTO dto) {
        return null;
    }

    @Override
    public AuthResponse registerUser(UserRequestDTO dto) {
        return null;
    }

    @Override
    public AuthResponse loginUser(AuthRequestDTO dto) {
        return null;
    }

    @Override
    public String logout(String token) {
        return null;
    }
}