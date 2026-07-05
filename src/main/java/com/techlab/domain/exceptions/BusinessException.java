package com.techlab.domain.exceptions;

/**
 * Excepción base para violaciones de reglas de negocio.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
