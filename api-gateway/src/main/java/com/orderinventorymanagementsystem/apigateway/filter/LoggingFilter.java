package com.orderinventorymanagementsystem.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String correlationId = getCorrelationId(exchange);
        String path = request.getURI().getRawPath();
        String method = request.getMethod().name();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String maskedAuth = authHeader != null ? authHeader.replaceAll("Bearer\\s+[^\\s]+", "Bearer [REDACTED]") : "none";

        logger.info("Incoming request method={}, path={}, correlationId={}, authorization={}", method, path, correlationId, maskedAuth);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            var status = exchange.getResponse().getStatusCode();
            logger.info("Completed request method={}, path={}, status={}, correlationId={}", method, path, status, correlationId);
        }));
    }

    private String getCorrelationId(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(CORRELATION_HEADER);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
