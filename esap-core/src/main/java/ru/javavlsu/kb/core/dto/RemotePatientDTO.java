package ru.javavlsu.kb.core.dto;

import java.time.LocalDate;

public record RemotePatientDTO(
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
    public String fullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
}
