package ru.javavlsu.kb.clinic.dto;

import java.time.LocalDate;
import java.util.List;

public record MedicalRecordResponseDTO(
        Long id,
        String record,
        String fioAndSpecializationDoctor,
        LocalDate date,
        List<AnalysisResponseDTO> analyzes
) {
}
