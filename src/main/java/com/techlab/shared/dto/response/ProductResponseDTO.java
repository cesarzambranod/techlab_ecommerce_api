package com.techlab.shared.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de productos.
 */
public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        String imageUrl,
        Integer stock,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
