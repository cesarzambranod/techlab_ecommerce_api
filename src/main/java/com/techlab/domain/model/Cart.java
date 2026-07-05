package com.techlab.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.SequencedCollection;

/**
 * Entidad de dominio que representa el carrito de compras de un usuario.
 * Utiliza SequencedCollection (Java 21+) para mantener orden de inserción.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private Long userId;

    @Builder.Default
    private SequencedCollection<CartItem> items = new ArrayList<>();

    public BigDecimal calculateTotal() {
        return items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addFirst(CartItem item) {
        items.addFirst(item);
    }

    public void addLast(CartItem item) {
        items.addLast(item);
    }

    public CartItem getFirst() {
        return items.getFirst();
    }

    public CartItem getLast() {
        return items.getLast();
    }
}
