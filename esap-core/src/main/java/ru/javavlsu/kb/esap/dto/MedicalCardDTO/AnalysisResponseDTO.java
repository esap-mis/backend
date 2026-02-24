package ru.javavlsu.kb.esap.dto.MedicalCardDTO;


import java.time.LocalDateTime;

public record AnalysisResponseDTO (
        Long id,
        String name,
        String result,
        LocalDateTime date
) {}
