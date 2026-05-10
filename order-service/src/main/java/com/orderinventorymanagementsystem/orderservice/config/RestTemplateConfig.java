package com.orderinventorymanagementsystem.orderservice.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    RestTemplate restTemplate() {
        // Configure timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))  // Connection timeout
                .setResponseTimeout(Timeout.ofSeconds(10)) // Read timeout
                .build();

        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
