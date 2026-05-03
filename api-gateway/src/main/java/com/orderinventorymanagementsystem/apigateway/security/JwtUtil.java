package com.orderinventorymanagementsystem.apigateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims validateToken(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token expired");
        } catch (UnsupportedJwtException ex) {
            throw new RuntimeException("Unsupported token");
        } catch (MalformedJwtException ex) {
            throw new RuntimeException("Invalid token");
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Token is empty");
        }
    }

    public String extractUserId(String token) {
        return validateToken(token).get("userId", String.class);
    }

    public String extractTenantId(String token) {
        return validateToken(token).get("tenantId", String.class);
    }

    public String extractRole(String token) {
        return validateToken(token).get("role", String.class);
    }

    public String extractUsername(String token) {
        return validateToken(token).getSubject();
    }
}