package com.techlab.integration;

import com.techlab.application.ports.in.OrderUseCasePorts;
import com.techlab.domain.enums.OrderStatus;
import com.techlab.shared.dto.request.OrderItemRequestDTO;
import com.techlab.shared.dto.request.OrderRequestDTO;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Tests de integración para OrderController")
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderUseCasePorts orderUseCase;

    @Test
    @DisplayName("GET /api/orders debe retornar lista de pedidos")
    void getAllOrders_ReturnsListOfOrders() throws Exception {
        List<OrderItemResponseDTO> items = Arrays.asList(
                new OrderItemResponseDTO(1L, 1L, "Producto 1", 2,
                        new BigDecimal("100.00"), new BigDecimal("200.00"))
        );

        List<OrderResponseDTO> orders = Arrays.asList(
                new OrderResponseDTO(1L, 1L, new BigDecimal("200.00"),
                        OrderStatus.PENDIENTE, items, LocalDateTime.now(), LocalDateTime.now())
        );

        when(orderUseCase.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDIENTE"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} debe retornar pedido por ID")
    void getOrderById_ReturnsOrder() throws Exception {
        List<OrderItemResponseDTO> items = Arrays.asList(
                new OrderItemResponseDTO(1L, 1L, "Producto Test", 1,
                        new BigDecimal("50.00"), new BigDecimal("50.00"))
        );

        OrderResponseDTO order = new OrderResponseDTO(
                1L, 1L, new BigDecimal("50.00"),
                OrderStatus.PENDIENTE, items, LocalDateTime.now(), LocalDateTime.now());

        when(orderUseCase.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("POST /api/orders debe crear un pedido")
    void createOrder_ReturnsCreatedOrder() throws Exception {
        List<OrderItemResponseDTO> items = Arrays.asList(
                new OrderItemResponseDTO(1L, 1L, "Producto 1", 2,
                        new BigDecimal("100.00"), new BigDecimal("200.00"))
        );

        OrderResponseDTO response = new OrderResponseDTO(
                2L, 1L, new BigDecimal("200.00"),
                OrderStatus.PENDIENTE, items, LocalDateTime.now(), LocalDateTime.now());

        when(orderUseCase.createOrder(any(OrderRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "userId": 1,
                                    "items": [
                                        {
                                            "productId": 1,
                                            "quantity": 2
                                        }
                                    ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.status").value("PENDIENTE"));
    }

    @Test
    @DisplayName("PUT /api/orders/{id}/status debe actualizar estado del pedido")
    void updateOrderStatus_ReturnsUpdatedOrder() throws Exception {
        List<OrderItemResponseDTO> items = Arrays.asList(
                new OrderItemResponseDTO(1L, 1L, "Producto 1", 2,
                        new BigDecimal("100.00"), new BigDecimal("200.00"))
        );

        OrderResponseDTO response = new OrderResponseDTO(
                1L, 1L, new BigDecimal("200.00"),
                OrderStatus.CONFIRMADO, items, LocalDateTime.now(), LocalDateTime.now());

        when(orderUseCase.updateOrderStatus(eq(1L), eq("CONFIRMADO"))).thenReturn(response);

        mockMvc.perform(put("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "CONFIRMADO"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMADO"));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} debe cancelar un pedido")
    void cancelOrder_ReturnsNoContent() throws Exception {
        doNothing().when(orderUseCase).cancelOrder(1L);

        mockMvc.perform(delete("/api/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/orders/user/{userId} debe retornar pedidos del usuario")
    void getOrdersByUserId_ReturnsUserOrders() throws Exception {
        List<OrderItemResponseDTO> items = Arrays.asList(
                new OrderItemResponseDTO(1L, 1L, "Producto 1", 1,
                        new BigDecimal("100.00"), new BigDecimal("100.00"))
        );

        List<OrderResponseDTO> orders = Arrays.asList(
                new OrderResponseDTO(1L, 1L, new BigDecimal("100.00"),
                        OrderStatus.PENDIENTE, items, LocalDateTime.now(), LocalDateTime.now())
        );

        when(orderUseCase.getOrdersByUserId(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userId").value(1));
    }
}
