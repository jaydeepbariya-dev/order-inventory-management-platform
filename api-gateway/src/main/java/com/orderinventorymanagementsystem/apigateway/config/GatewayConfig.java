package com.orderinventorymanagementsystem.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public KeyResolver authorizationKeyResolver() {
        return exchange -> resolveKey(exchange);
    }

    private Mono<String> resolveKey(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(token -> !token.isBlank())
                .switchIfEmpty(Mono.justOrEmpty(exchange.getRequest().getRemoteAddress())
                        .map(remote -> remote.getAddress().getHostAddress()));
    }
}
