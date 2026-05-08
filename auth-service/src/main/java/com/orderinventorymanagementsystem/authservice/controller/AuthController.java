package com.orderinventorymanagementsystem.authservice.controller;

import com.orderinventorymanagementsystem.authservice.dto.*;
import com.orderinventorymanagementsystem.authservice.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Authentication APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register Tenant", description = "Registers a new tenant")
    @PostMapping("/tenant")
    public ResponseEntity<TenantResponseDTO> registerTenant(@RequestBody TenantRequestDTO dto) {
        TenantResponseDTO res = authService.registerTenant(dto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = "Register User", description = "Registers a new user")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRequestDTO dto) {
        AuthResponse res = authService.registerUser(dto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = "Login", description = "Authenticates a user and returns a JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequestDTO dto) {
        AuthResponse res = authService.loginUser(dto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Logout", description = "Invalidates the current JWT token")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String res = authService.logout(token);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(summary = "Refresh Token", description = "Refreshes the JWT token using a refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        AuthResponse res = authService.refreshToken(refreshTokenRequestDTO);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}