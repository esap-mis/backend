package ru.javavlsu.kb.esap.dto.chat;

import java.time.LocalDateTime;

/**
 * ChatResponseDTO 17.02.2026 Alexey Karabanov
 * Copyright (c) 2026 WINGS.
 */
public record ChatResponseDTO(
        String sessionId,
        String message,
        MessageType type,
        LocalDateTime timestamp
) {}

