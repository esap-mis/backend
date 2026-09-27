package ru.javavlsu.kb.clinic.dto;

import java.time.LocalDateTime;

public record AnalysisResponseDTO(
        Long id,
        String name,
        String result,
        LocalDateTime date
) {
}
