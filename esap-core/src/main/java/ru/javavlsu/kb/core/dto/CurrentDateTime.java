package ru.javavlsu.kb.core.dto;

import java.time.LocalDateTime;

public record CurrentDateTime(
        String date,
        String time,
        String dayOfWeek,
        LocalDateTime fullDateTime
) {
}
