package com.orderinventorymanagementsystem.authservice.service.impl;

import com.orderinventorymanagementsystem.authservice.dto.*;
import com.orderinventorymanagementsystem.authservice.entity.*;
import com.orderinventorymanagementsystem.authservice.enums.TenantStatus;
import com.orderinventorymanagementsystem.authservice.repository.*;
import com.orderinventorymanagementsystem.authservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // =========================
    // REGISTER TENANT
    // =========================
    @Test
    void registerTenant_success() {

        TenantRequestDTO dto = new TenantRequestDTO();
        dto.setName("TestTenant");

        when(tenantRepository.findByName("TestTenant"))
                .thenReturn(Optional.empty());

        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID());
        tenant.setName("TestTenant");
        tenant.setStatus(TenantStatus.ACTIVE);

        when(tenantRepository.save(any())).thenReturn(tenant);

        TenantResponseDTO response = authService.registerTenant(dto);

        assertNotNull(response);
        assertEquals("TestTenant", response.getName());
    }

    // =========================
    // REGISTER USER
    // =========================
    @Test
    void registerUser_success() {

        UUID tenantId = UUID.randomUUID();

        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("John");
        dto.setEmail("john@test.com");
        dto.setPassword("pass");
        dto.setTenantId(tenantId);

        when(tenantRepository.findById(tenantId))
                .thenReturn(Optional.of(new Tenant()));

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("john@test.com");

        when(userRepository.save(any())).thenReturn(user);

        when(jwtUtil.generateToken(any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh-token");

        AuthResponse response = authService.registerUser(dto);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    // =========================
    // LOGIN USER
    // =========================
    @Test
    void loginUser_success() {

        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("john@test.com");
        dto.setPassword("pass");

        User user = new User();
        user.setEmail("john@test.com");
        user.setPasswordHash("encoded");

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("pass", "encoded"))
                .thenReturn(true);

        when(jwtUtil.generateToken(user)).thenReturn("access");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh");

        AuthResponse response = authService.loginUser(dto);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    // =========================
    // REFRESH TOKEN
    // =========================
    @Test
    void refreshToken_success() {

        RefreshTokenRequestDTO dto = new RefreshTokenRequestDTO();
        dto.setRefreshToken("old-refresh");

        RefreshToken token = new RefreshToken();
        token.setToken("old-refresh");
        token.setUserId(UUID.randomUUID());
        token.setExpiryDate(Instant.now().plusSeconds(1000));

        when(refreshTokenRepository.findByToken("old-refresh"))
                .thenReturn(Optional.of(token));

        User user = new User();
        user.setId(token.getUserId());

        when(userRepository.findById(token.getUserId()))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(user)).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken(user)).thenReturn("new-refresh");

        AuthResponse response = authService.refreshToken(dto);

        assertEquals("new-access", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());

        verify(refreshTokenRepository, times(1)).delete(token);
    }

    // =========================
    // LOGOUT
    // =========================
    @Test
    void logout_success() {

        String token = "Bearer dummy.jwt.token";

        when(jwtUtil.extractUsername(anyString()))
                .thenReturn("john@test.com");

        User user = new User();
        user.setId(UUID.randomUUID());

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        String response = authService.logout(token);

        verify(refreshTokenRepository, times(1))
                .deleteByUserId(user.getId());

        assertEquals("Logged out successfully", response);
    }
}