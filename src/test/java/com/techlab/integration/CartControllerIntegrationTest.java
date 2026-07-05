package com.techlab.integration;

import com.techlab.application.ports.in.CartUseCasePorts;
import com.techlab.domain.enums.OrderStatus;
import com.techlab.shared.dto.request.CartItemRequestDTO;
import com.techlab.shared.dto.request.UpdateCartItemRequestDTO;
import com.techlab.shared.dto.response.CartItemResponseDTO;
import com.techlab.shared.dto.response.CartResponseDTO;
import com.techlab.shared.dto.response.OrderItemResponseDTO;
import com.techlab.shared.dto.response.OrderResponseDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de integración para CartController")
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartUseCasePorts cartUseCase;

    @Test
    @DisplayName("GET /api/cart/{userId} debe retornar carrito")
    void getCart_ReturnsCart() throws Exception {
        List<CartItemResponseDTO> items = Arrays.asList(
                new CartItemResponseDTO(1L, 1L, "Producto 1", "http://example.com/img.jpg",
                        2, new BigDecimal("100.00"), new BigDecimal("200.00"))
        );

        CartResponseDTO cart = new CartResponseDTO(1L, items, new BigDecimal("200.00"));

        when(cartUseCase.getCart(1L)).thenReturn(cart);

        mockMvc.perform(get("/api/cart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.total").value(200.00));
    }

    @Test
    @DisplayName("POST /api/cart/{userId}/add debe agregar item al carrito")
    void addItemToCart_ReturnsUpdatedCart() throws Exception {
        List<CartItemResponseDTO> items = Arrays.asList(
                new CartItemResponseDTO(1L, 1L, "Producto 1", "http://example.com/img.jpg",
                        3, new BigDecimal("100.00"), new BigDecimal("300.00"))
        );

        CartResponseDTO cart = new CartResponseDTO(1L, items, new BigDecimal("300.00"));

        when(cartUseCase.addItemToCart(eq(1L), any(CartItemRequestDTO.class))).thenReturn(cart);

        mockMvc.perform(post("/api/cart/1/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "productId": 1,
                                    "quantity": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(3));
    }

    @Test
    @DisplayName("PUT /api/cart/{userId}/update/{productId} debe actualizar cantidad")
    void updateCartItem_ReturnsUpdatedCart() throws Exception {
        List<CartItemResponseDTO> items = Arrays.asList(
                new CartItemResponseDTO(1L, 1L, "Producto 1", "http://example.com/img.jpg",
                        5, new BigDecimal("100.00"), new BigDecimal("500.00"))
        );

        CartResponseDTO cart = new CartResponseDTO(1L, items, new BigDecimal("500.00"));

        when(cartUseCase.updateCartItem(eq(1L), eq(1L), eq(5))).thenReturn(cart);

        mockMvc.perform(put("/api/cart/1/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "quantity": 5
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].quantity").value(5));
    }

    @Test
    @DisplayName("DELETE /api/cart/{userId}/remove/{productId} debe eliminar item")
    void removeItemFromCart_ReturnsUpdatedCart() throws Exception {
        CartResponseDTO cart = new CartResponseDTO(1L, new ArrayList<>(), BigDecimal.ZERO);

        when(cartUseCase.removeItemFromCart(1L, 1L)).thenReturn(cart);

        mockMvc.perform(delete("/api/cart/1/remove/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    @DisplayName("POST /api/cart/{userId}/checkout debe crear orden")
    void checkout_ReturnsOrder() throws Exception {
        List<OrderItemResponseDTO> orderItems = Arrays.asList(
                new OrderItemResponseDTO(1L, 1L, "Producto 1",
                        2, new BigDecimal("100.00"), new BigDecimal("200.00"))
        );

        OrderResponseDTO order = new OrderResponseDTO(
                1L, 1L, new BigDecimal("200.00"), OrderStatus.PENDIENTE,
                orderItems, LocalDateTime.now(), LocalDateTime.now()
        );

        when(cartUseCase.checkout(1L)).thenReturn(order);

        mockMvc.perform(post("/api/cart/1/checkout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.status").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(200.00));
    }
}
