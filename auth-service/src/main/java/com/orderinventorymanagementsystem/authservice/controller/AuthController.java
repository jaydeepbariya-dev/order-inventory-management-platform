package com.orderinventorymanagementsystem.authservice.controller;

import com.orderinventorymanagementsystem.authservice.dto.*;
import com.orderinventorymanagementsystem.authservice.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/tenant")
    public TenantResponseDTO registerTenant(@RequestBody TenantRequestDTO dto) {
        return authService.registerTenant(dto);
    }

    @PostMapping("/register")
    public AuthResponse registerUser(@RequestBody UserRequestDTO dto) {
        return authService.registerUser(dto);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequestDTO dto) {
        return authService.loginUser(dto);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String token) {
        return authService.logout(token);
    }
}