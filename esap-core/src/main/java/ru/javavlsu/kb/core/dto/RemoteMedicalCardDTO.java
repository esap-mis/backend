package ru.javavlsu.kb.core.dto;

import java.time.LocalDate;
import java.util.List;

public record RemoteMedicalCardDTO(
        Long id,
        List<RemoteMedicalRecordDTO> medicalRecord
) {
}
