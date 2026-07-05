package com.techlab.shared.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar el estado de un pedido.
 */
public record UpdateOrderStatusRequestDTO(
        @NotBlank(message = "El estado es obligatorio")
        String status
) {
}
