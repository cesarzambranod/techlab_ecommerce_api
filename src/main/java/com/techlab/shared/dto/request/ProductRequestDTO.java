package com.techlab.shared.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para solicitudes de creación/actualización de productos.
 */
public record ProductRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String name,

        String description,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser positivo")
        BigDecimal price,

        String category,

        @Pattern(regexp = "^(https?://.*)?$", message = "La URL de imagen debe ser válida")
        String imageUrl,

        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stock
) {
}
