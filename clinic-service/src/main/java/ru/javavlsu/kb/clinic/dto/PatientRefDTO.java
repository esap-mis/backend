package ru.javavlsu.kb.clinic.dto;

import java.time.LocalDate;

public record PatientRefDTO(
        Long id,
        String firstName,
        String patronymic,
        String lastName,
        LocalDate birthDate,
        int gender,
        String address,
        String phoneNumber
) {
}
