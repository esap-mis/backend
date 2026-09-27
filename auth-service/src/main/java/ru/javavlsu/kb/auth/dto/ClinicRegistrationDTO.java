package ru.javavlsu.kb.auth.dto;

import jakarta.validation.Valid;

public record ClinicRegistrationDTO(
        @Valid ClinicRegistration clinic,
        @Valid DoctorRegistration doctor
) {
}
