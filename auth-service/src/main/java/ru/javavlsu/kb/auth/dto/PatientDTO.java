package ru.javavlsu.kb.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record PatientDTO(
        Long id,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String patronymic,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull LocalDate birthDate,
        @Max(value = 2, message = "Неверно указан пол") @Min(value = 1, message = "Неверно указан пол") int gender,
        @Size(max = 200) String address,
        @NotBlank @Size(max = 20) String phoneNumber,
        @NotBlank @Email @Size(max = 100) String email
) {
}
