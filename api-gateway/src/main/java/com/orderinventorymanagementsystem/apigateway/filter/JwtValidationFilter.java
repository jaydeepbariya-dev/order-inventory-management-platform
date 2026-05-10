package com.orderinventorymanagementsystem.apigateway.filter;

import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;

@Component
public class JwtValidationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationFilter.class);
    private static final String AUTH_PATH_PREFIX = "/api/v1/auth";
    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    private final String jwtSecret;

    public JwtValidationFilter(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith(AUTH_PATH_PREFIX)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return reject(exchange, "Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);

            String subject = claims.getBody().getSubject();
            if (subject == null || subject.isBlank()) {
                return reject(exchange, "JWT token contains no subject");
            }

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .header(HttpHeaders.USER_AGENT, exchange.getRequest().getHeaders().getFirst(HttpHeaders.USER_AGENT))
                    .header(CORRELATION_HEADER, getCorrelationId(exchange))
                    .header("X-User-Id", subject)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JwtException | IllegalArgumentException exception) {
            logger.warn("JWT validation failed: {}", exception.getMessage());
            return reject(exchange, "JWT token invalid or expired");
        }
    }

    private String getCorrelationId(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(CORRELATION_HEADER);
        return header != null ? header : "unknown";
    }

    private Mono<Void> reject(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = String.format("{\"error\":\"%s\"}", message);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
