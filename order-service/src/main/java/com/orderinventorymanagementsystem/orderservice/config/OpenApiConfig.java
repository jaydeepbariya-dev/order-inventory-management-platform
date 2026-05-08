package com.orderinventorymanagementsystem.orderservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Order Service API")
                                .version("1.0")
                                .description("Order Management APIs")

                                .contact(
                                        new Contact()
                                                .name("Jaydeep Bariya")
                                                .email("jaydeepbariya59@gmail.com"))

                                .license(
                                        new License()
                                                .name("Apache 2.0")));
    }
}