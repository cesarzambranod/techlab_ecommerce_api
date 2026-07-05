package com.techlab.shared.dto.response;

import com.techlab.domain.enums.UserRole;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de usuarios.
 */
public record UserResponseDTO(
        Long id,
        String username,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {
}
