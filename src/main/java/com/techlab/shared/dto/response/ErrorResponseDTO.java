package com.techlab.shared.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respuestas de error.
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, Object> details
) {
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    public ErrorResponseDTO(int status, String error, String message, String path, Map<String, Object> details) {
        this(LocalDateTime.now(), status, error, message, path, details);
    }
}
