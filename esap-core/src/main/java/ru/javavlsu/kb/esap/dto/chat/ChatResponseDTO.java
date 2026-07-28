package ru.javavlsu.kb.esap.dto.chat;

import java.time.LocalDateTime;

public record ChatResponseDTO(
        String sessionId,
        String message,
        MessageType type,
        LocalDateTime timestamp
) {}

