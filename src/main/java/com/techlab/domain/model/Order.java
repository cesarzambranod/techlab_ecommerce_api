package com.techlab.domain.model;

import com.techlab.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.SequencedCollection;

/**
 * Entidad de dominio que representa un pedido en el sistema.
 * Utiliza SequencedCollection (Java 21+) para mantener orden de inserción en items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Long id;
    private Long userId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private SequencedCollection<OrderItem> items = new ArrayList<>();
}
