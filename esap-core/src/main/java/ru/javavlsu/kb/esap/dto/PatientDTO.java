package ru.javavlsu.kb.esap.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.model.Clinic;

import java.time.LocalDate;

public record PatientDTO(
        Long id,
        @NotBlank
        @Size(max = 100)
        String firstName,
        @NotBlank
        @Size(max = 100)
        String patronymic,
        @NotBlank
        @Size(max = 100)
        String lastName,
        @NotNull
        LocalDate birthDate,
        @Max(value = 2, message = "Не верно указан пол")
        @Min(value = 1, message = "Не верно указан пол")
        int gender,
        @Size(max = 200)
        String address,
        @NotBlank
        @Size(max = 20)
        String phoneNumber,
        @NotBlank
        @Email
        @Size(max = 100)
        String email,
        Clinic clinic
) {
}
