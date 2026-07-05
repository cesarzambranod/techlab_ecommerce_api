package com.techlab.shared.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de autenticación.
 */
public record AuthRequestDTO(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
