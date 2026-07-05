package com.techlab.domain.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un pedido solicitado.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Pedido no encontrado con ID: " + id);
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
