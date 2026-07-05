package com.techlab.domain.exceptions;

/**
 * Excepción lanzada cuando los datos del producto son inválidos.
 */
public class InvalidProductDataException extends RuntimeException {

    public InvalidProductDataException(String message) {
        super(message);
    }
}
