package com.orderinventorymanagementsystem.authservice.service.impl;

import com.orderinventorymanagementsystem.authservice.dto.*;
import com.orderinventorymanagementsystem.authservice.entity.*;
import com.orderinventorymanagementsystem.authservice.enums.TenantStatus;
import com.orderinventorymanagementsystem.authservice.mapper.TenantMapper;
import com.orderinventorymanagementsystem.authservice.repository.*;
import com.orderinventorymanagementsystem.authservice.security.JwtUtil;
import com.orderinventorymanagementsystem.authservice.service.AuthService;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
            TenantRepository tenantRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public TenantResponseDTO registerTenant(TenantRequestDTO dto) {

        tenantRepository.findByName(dto.getName())
                .ifPresent(t -> {
                    throw new RuntimeException("Tenant already exists");
                });

        Tenant tenant = new Tenant();
        tenant.setName(dto.getName());
        tenant.setEmail(dto.getEmail());
        tenant.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        tenant.setStatus(TenantStatus.ACTIVE);

        Tenant saved = tenantRepository.save(tenant);

        return TenantMapper.toDTO(saved);
    }

    @Override
    public AuthResponse registerUser(UserRequestDTO dto) {

        tenantRepository.findById(dto.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        userRepository.findByEmail(dto.getEmail())
                .ifPresent(u -> {
                    throw new RuntimeException("User already exists");
                });

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setTenantId(dto.getTenantId());
        user.setActive(true);

        User savedUser = userRepository.save(user);

        String accessToken = jwtUtil.generateToken(savedUser);
        String refreshToken = jwtUtil.generateRefreshToken(savedUser);

        saveRefreshToken(savedUser.getId(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse loginUser(AuthRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        saveRefreshToken(user.getId(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequestDTO refreshTokenRequestDTO) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshTokenRequestDTO.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.delete(token);

        String newRefreshToken = jwtUtil.generateRefreshToken(user);
        String newAccessToken = jwtUtil.generateToken(user);

        saveRefreshToken(user.getId(), newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public String logout(String token) {

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteByUserId(user.getId());

        return "Logged out successfully";
    }

    private void saveRefreshToken(UUID userId, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60));

        refreshTokenRepository.save(refreshToken);
    }
}