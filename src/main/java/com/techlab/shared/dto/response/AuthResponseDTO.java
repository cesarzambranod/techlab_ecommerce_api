package com.techlab.shared.dto.response;

/**
 * DTO para respuestas de autenticación.
 */
public record AuthResponseDTO(
        String token,
        String type,
        Long userId,
        String username,
        String role
) {
    public AuthResponseDTO(String token, Long userId, String username, String role) {
        this(token, "Bearer", userId, username, role);
    }
}
