package ru.javavlsu.kb.auth.dto;

import jakarta.validation.constraints.Size;

public record DoctorDTO(
        Long id,
        String login,
        String firstName,
        @Size(max = 100) String patronymic,
        String lastName,
        String specialization,
        int gender,
        ClinicDTO clinic
) {
}
