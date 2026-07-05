package com.techlab.domain.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un usuario solicitado.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
