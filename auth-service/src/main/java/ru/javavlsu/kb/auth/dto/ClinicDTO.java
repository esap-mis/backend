package ru.javavlsu.kb.auth.dto;

public record ClinicDTO(
        Long id,
        String name,
        String address,
        String phoneNumber
) {
}
