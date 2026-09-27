package ru.javavlsu.kb.core.dto;

import java.time.LocalDate;

public record AppointmentRequestDTO(
        Long patientId,
        LocalDate date,
        java.time.LocalTime startAppointments
) {
}
