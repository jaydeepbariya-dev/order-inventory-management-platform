package com.orderinventorymanagementsystem.productservice.impl;

import com.orderinventorymanagementsystem.productservice.dto.*;
import com.orderinventorymanagementsystem.productservice.dto.client.InventoryRequestDTO;
import com.orderinventorymanagementsystem.productservice.entity.Product;
import com.orderinventorymanagementsystem.productservice.enums.StockStatus;
import com.orderinventorymanagementsystem.productservice.exception.ProductAlreadyExistsException;
import com.orderinventorymanagementsystem.productservice.exception.ProductNotFoundException;
import com.orderinventorymanagementsystem.productservice.exception.UnauthorizedException;
import com.orderinventorymanagementsystem.productservice.repository.ProductRepository;
import com.orderinventorymanagementsystem.productservice.service.impl.ProductServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

        @Mock
        private ProductRepository productRepository;

        @Mock
        private RestTemplate restTemplate;

        @InjectMocks
        private ProductServiceImpl productService;

        private Product product;
        private ProductRequestDTO requestDTO;
        private UUID productId;
        private UUID sellerId;
        private UUID tenantId;

        @BeforeEach
        void setUp() {

                productId = UUID.randomUUID();
                sellerId = UUID.randomUUID();
                tenantId = UUID.randomUUID();

                product = new Product();
                product.setId(productId);
                product.setName("Laptop");
                product.setDescription("Gaming Laptop");
                product.setPrice(50000.0);
                product.setStockStatus(StockStatus.IN_STOCK);
                product.setSellerId(sellerId);
                product.setTenantId(tenantId);

                requestDTO = new ProductRequestDTO();
                requestDTO.setName("Laptop");
                requestDTO.setDescription("Gaming Laptop");
                requestDTO.setPrice(50000.0);
                requestDTO.setStockStatus("IN_STOCK");
                requestDTO.setQuantity(10);
        }

        @Test
        void createProduct_success() {

                when(productRepository.findByName("Laptop"))
                                .thenReturn(Optional.empty());

                when(productRepository.save(any(Product.class)))
                                .thenReturn(product);

                when(restTemplate.postForObject(
                                anyString(),
                                any(InventoryRequestDTO.class),
                                eq(String.class)))
                                .thenReturn("Inventory created");

                ProductResponseDTO response = productService.createProduct(requestDTO, sellerId, tenantId, "SELLER");

                assertNotNull(response);
                assertEquals("Laptop", response.getName());

                verify(productRepository, times(1)).save(any(Product.class));
        }

        @Test
        void createProduct_shouldThrowException_whenRoleIsNotSeller() {

                assertThrows(UnauthorizedException.class,
                                () -> productService.createProduct(requestDTO, sellerId, tenantId, "BUYER"));
        }

        @Test
        void createProduct_shouldThrowException_whenProductExists() {

                when(productRepository.findByName("Laptop"))
                                .thenReturn(Optional.of(product));

                assertThrows(ProductAlreadyExistsException.class,
                                () -> productService.createProduct(requestDTO, sellerId, tenantId, "SELLER"));
        }

        @Test
        void getProductById_success() {

                when(productRepository.findByIdAndTenantId(productId, tenantId))
                                .thenReturn(Optional.of(product));

                ProductResponseDTO response = productService.getProductById(productId, tenantId);

                assertNotNull(response);
                assertEquals(productId, response.getId());
        }

        @Test
        void getProductById_shouldThrowException_whenNotFound() {

                when(productRepository.findByIdAndTenantId(productId, tenantId))
                                .thenReturn(Optional.empty());

                assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId, tenantId));
        }

        @Test
        void getAllProducts_success() {

                when(productRepository.findByTenantId(tenantId))
                                .thenReturn(List.of(product));

                List<ProductResponseDTO> response = productService.getAllProducts(tenantId);

                assertEquals(1, response.size());
        }

        @Test
        void deleteProduct_success() {

                when(productRepository.findByIdAndTenantId(productId, tenantId))
                                .thenReturn(Optional.of(product));

                productService.deleteProduct(productId, sellerId, tenantId, "SELLER");

                verify(productRepository, times(1)).delete(product);
        }

        @Test
        void searchProducts_success() {

                when(productRepository.findByTenantIdAndNameContainingIgnoreCase(
                                tenantId,
                                "Laptop"))
                                .thenReturn(List.of(product));

                List<ProductResponseDTO> response = productService.searchProducts("Laptop", tenantId);

                assertEquals(1, response.size());
        }

        @Test
        void getProductsByFilter_success() {

                Page<Product> page = new PageImpl<>(List.of(product));

                when(productRepository.findAll(
                                ArgumentMatchers.any(Specification.class),
                                ArgumentMatchers.any(Pageable.class)))
                                .thenReturn(page);

                ProductFilterRequestDTO filter = new ProductFilterRequestDTO();

                filter.setPage(0);
                filter.setSize(10);
                filter.setSortBy("createdAt");
                filter.setSortDir("desc");

                PageResponseDTO<ProductResponseDTO> response = productService.getProductsByFilter(filter, tenantId);

                assertEquals(1, response.getContent().size());
        }
}