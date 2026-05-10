package com.orderinventorymanagementsystem.apigateway.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String HEADER_NAME = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(HEADER_NAME);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
            logger.debug("Generated correlation id={}", correlationId);
        }

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(HEADER_NAME, correlationId)
                .build();

        exchange.getResponse().getHeaders().set(HEADER_NAME, correlationId);
        exchange.getAttributes().put(HEADER_NAME, correlationId);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
