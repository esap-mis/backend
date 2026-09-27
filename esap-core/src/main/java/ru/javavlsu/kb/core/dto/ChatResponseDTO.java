package ru.javavlsu.kb.core.dto;

import java.time.LocalDateTime;

public record ChatResponseDTO(
        String sessionId,
        String message,
        MessageType type,
        LocalDateTime timestamp
) {
}
