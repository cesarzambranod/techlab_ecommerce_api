package com.techlab.shared.dto.response;

import com.techlab.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de pedidos.
 */
public record OrderResponseDTO(
        Long id,
        Long userId,
        BigDecimal total,
        OrderStatus status,
        List<OrderItemResponseDTO> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
