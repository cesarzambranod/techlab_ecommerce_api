package com.techlab.shared.dto.response;

import java.math.BigDecimal;

/**
 * DTO para respuestas de items de pedido.
 */
public record OrderItemResponseDTO(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
