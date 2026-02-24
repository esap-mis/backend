package ru.javavlsu.kb.esap.dto;

import jakarta.validation.constraints.Size;

public record DoctorResponseDTO (
        Long id,
        String firstName,
        @Size(max = 100)
        String patronymic,
        String lastName,
        String specialization,
        int gender,
        ClinicDTO clinic
) {}
