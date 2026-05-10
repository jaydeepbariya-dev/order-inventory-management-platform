package com.orderinventorymanagementsystem.productservice.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public RetryRegistry retryRegistry() {
        return RetryRegistry.ofDefaults();
    }

    @Bean
    public Retry inventoryServiceRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(1000))
                .retryOnException(throwable -> {
                    // Retry on connection timeouts, read timeouts, and 5xx errors
                    String message = throwable.getMessage();
                    return message != null && (
                            message.contains("Connection timed out") ||
                            message.contains("Read timed out") ||
                            message.contains("500") ||
                            message.contains("502") ||
                            message.contains("503") ||
                            message.contains("504")
                    );
                })
                .build();

        return Retry.of("inventoryServiceRetry", config);
    }
}