package com.techlab.shared.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO para solicitudes de creación de pedidos.
 */
public record OrderRequestDTO(
        @NotNull(message = "El ID de usuario es obligatorio")
        Long userId,

        @NotNull(message = "Los items del pedido son obligatorios")
        @Valid
        List<OrderItemRequestDTO> items
) {
}
