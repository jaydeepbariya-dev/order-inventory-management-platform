package com.orderinventorymanagementsystem.inventoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Inventory Service API")
                                .version("1.0")
                                .description("Inventory Management APIs")

                                .contact(
                                        new Contact()
                                                .name("Jaydeep Bariya")
                                                .email("jaydeepbariya59@gmail.com"))

                                .license(
                                        new License()
                                                .name("Apache 2.0")));
    }
}
