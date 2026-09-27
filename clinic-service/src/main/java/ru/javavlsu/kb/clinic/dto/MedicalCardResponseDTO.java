package ru.javavlsu.kb.clinic.dto;

import java.util.List;

public record MedicalCardResponseDTO(
        Long id,
        List<MedicalRecordResponseDTO> medicalRecord,
        PatientRefDTO patient
) {
}
