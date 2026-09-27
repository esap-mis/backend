package ru.javavlsu.kb.core.dto;

import java.time.LocalDate;
import java.util.List;

public record RemoteMedicalRecordDTO(
        Long id,
        String record,
        String fioAndSpecializationDoctor,
        LocalDate date,
        List<?> analyzes
) {
}
