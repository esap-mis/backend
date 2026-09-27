package ru.javavlsu.kb.schedule.dto;

import java.time.LocalDate;

public record PatientRefDTO(
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
