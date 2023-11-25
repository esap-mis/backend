package ru.javavlsu.kb.esap.dto.auth;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClinicRegistrationDTO {
    @Valid
    private ClinicRegistration clinic;
    @Valid
    private DoctorRegistration doctor;
}
