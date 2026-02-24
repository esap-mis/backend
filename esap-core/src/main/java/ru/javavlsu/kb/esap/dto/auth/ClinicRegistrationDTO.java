package ru.javavlsu.kb.esap.dto.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record ClinicRegistrationDTO (
        @Valid
        ClinicRegistration clinic,
        @Valid
        DoctorRegistration doctor
) {}
