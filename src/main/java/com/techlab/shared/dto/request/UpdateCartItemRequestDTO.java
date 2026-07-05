package com.techlab.shared.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para actualizar la cantidad de un item en el carrito.
 */
public record UpdateCartItemRequestDTO(
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser positiva")
        Integer quantity
) {
}
