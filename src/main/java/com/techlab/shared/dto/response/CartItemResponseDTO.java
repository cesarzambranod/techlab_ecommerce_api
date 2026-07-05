package com.techlab.shared.dto.response;

import java.math.BigDecimal;

/**
 * DTO para respuestas de items del carrito.
 */
public record CartItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        String productImageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
