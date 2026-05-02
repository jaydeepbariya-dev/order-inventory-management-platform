package com.orderinventorymanagementsystem.authservice.controller;

import com.orderinventorymanagementsystem.authservice.dto.*;
import com.orderinventorymanagementsystem.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/tenant")
    public ResponseEntity<TenantResponseDTO> registerTenant(@RequestBody TenantRequestDTO dto) {
        TenantResponseDTO res = authService.registerTenant(dto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRequestDTO dto) {
        AuthResponse res = authService.registerUser(dto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequestDTO dto) {
        AuthResponse res = authService.loginUser(dto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String res = authService.logout(token);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        AuthResponse res = authService.refreshToken(refreshTokenRequestDTO);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}