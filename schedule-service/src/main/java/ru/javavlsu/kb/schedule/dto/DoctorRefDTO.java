package ru.javavlsu.kb.schedule.dto;

import jakarta.validation.constraints.Size;

public record DoctorRefDTO(
        Long id,
        String firstName,
        @Size(max = 100) String patronymic,
        String lastName,
        String specialization,
        int gender,
        Long clinicId
) {
}
