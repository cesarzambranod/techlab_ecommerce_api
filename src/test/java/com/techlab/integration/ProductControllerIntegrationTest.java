package com.techlab.integration;

import com.techlab.application.ports.in.ProductUseCasePorts;
import com.techlab.shared.dto.request.ProductRequestDTO;
import com.techlab.shared.dto.response.ProductResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de integración para ProductController")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductUseCasePorts productUseCase;

    @Test
    @DisplayName("GET /api/products debe retornar lista de productos")
    void getAllProducts_ReturnsListOfProducts() throws Exception {
        List<ProductResponseDTO> products = Arrays.asList(
                new ProductResponseDTO(1L, "Producto 1", "Descripción 1",
                        new BigDecimal("100.00"), "Categoría 1", "http://example.com/img1.jpg",
                        10, LocalDateTime.now(), LocalDateTime.now()),
                new ProductResponseDTO(2L, "Producto 2", "Descripción 2",
                        new BigDecimal("200.00"), "Categoría 2", "http://example.com/img2.jpg",
                        20, LocalDateTime.now(), LocalDateTime.now())
        );

        when(productUseCase.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Producto 1"))
                .andExpect(jsonPath("$[1].name").value("Producto 2"));
    }

    @Test
    @DisplayName("GET /api/products/{id} debe retornar producto por ID")
    void getProductById_ReturnsProduct() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO(
                1L, "Producto Test", "Descripción Test",
                new BigDecimal("150.00"), "Electrónica", "http://example.com/img.jpg",
                15, LocalDateTime.now(), LocalDateTime.now());

        when(productUseCase.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Producto Test"))
                .andExpect(jsonPath("$.price").value(150.00));
    }

    @Test
    @DisplayName("POST /api/products debe crear un producto")
    void createProduct_ReturnsCreatedProduct() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO(
                "Nuevo Producto", "Nueva Descripción",
                new BigDecimal("300.00"), "Tecnología",
                "http://example.com/nueva.jpg", 25);

        ProductResponseDTO response = new ProductResponseDTO(
                3L, "Nuevo Producto", "Nueva Descripción",
                new BigDecimal("300.00"), "Tecnología",
                "http://example.com/nueva.jpg", 25,
                LocalDateTime.now(), LocalDateTime.now());

        when(productUseCase.createProduct(any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Nuevo Producto",
                                    "description": "Nueva Descripción",
                                    "price": 300.00,
                                    "category": "Tecnología",
                                    "imageUrl": "http://example.com/nueva.jpg",
                                    "stock": 25
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Nuevo Producto"));
    }

    @Test
    @DisplayName("PUT /api/products/{id} debe actualizar un producto")
    void updateProduct_ReturnsUpdatedProduct() throws Exception {
        ProductRequestDTO request = new ProductRequestDTO(
                "Producto Actualizado", "Descripción Actualizada",
                new BigDecimal("400.00"), "Gaming",
                "http://example.com/actualizado.jpg", 30);

        ProductResponseDTO response = new ProductResponseDTO(
                1L, "Producto Actualizado", "Descripción Actualizada",
                new BigDecimal("400.00"), "Gaming",
                "http://example.com/actualizado.jpg", 30,
                LocalDateTime.now(), LocalDateTime.now());

        when(productUseCase.updateProduct(any(Long.class), any(ProductRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Producto Actualizado",
                                    "description": "Descripción Actualizada",
                                    "price": 400.00,
                                    "category": "Gaming",
                                    "imageUrl": "http://example.com/actualizado.jpg",
                                    "stock": 30
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Producto Actualizado"))
                .andExpect(jsonPath("$.price").value(400.00));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} debe eliminar un producto")
    void deleteProduct_ReturnsNoContent() throws Exception {
        doNothing().when(productUseCase).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
