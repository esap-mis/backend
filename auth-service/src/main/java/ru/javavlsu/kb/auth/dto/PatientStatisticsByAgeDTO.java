package ru.javavlsu.kb.auth.dto;

public record PatientStatisticsByAgeDTO(
        int child,
        int adult,
        int elderly
) {
}
