package ru.javavlsu.kb.esap.dto.notifications;

import ru.javavlsu.kb.esap.model.Patient;

/**
 * PatientCreatedEvent 24.07.2026 thewyolar
 * Copyright (c) 2026.
 */
public record PatientCreatedEvent(
        String email,
        String firstName,
        String login,
        String password,
        String clinicName
) {
    public static PatientCreatedEvent from(Patient patient) {
        return new PatientCreatedEvent(
                patient.getEmail(),
                patient.getFirstName(),
                patient.getLogin(),
                patient.getPassword(),
                patient.getClinic().getName()
        );
    }
}
