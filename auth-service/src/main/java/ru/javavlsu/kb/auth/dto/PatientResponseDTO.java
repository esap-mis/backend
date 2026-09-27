package ru.javavlsu.kb.auth.dto;

import java.time.LocalDate;

public record PatientResponseDTO(
        Long id,
        String firstName,
        String patronymic,
        String lastName,
        LocalDate birthDate,
        int gender,
        String address,
        String phoneNumber,
        String email
) {
}
