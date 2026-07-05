package com.techlab.shared.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para respuestas del carrito.
 */
public record CartResponseDTO(
        Long userId,
        List<CartItemResponseDTO> items,
        BigDecimal total
) {
}
