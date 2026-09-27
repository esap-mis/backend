package ru.javavlsu.kb.core.dto;

public record RemoteDoctorDTO(
        Long id,
        String firstName,
        String patronymic,
        String lastName,
        String specialization,
        int gender
) {
    public String fullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
}
