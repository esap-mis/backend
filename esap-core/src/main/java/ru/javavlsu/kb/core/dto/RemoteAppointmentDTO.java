package ru.javavlsu.kb.core.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record RemoteAppointmentDTO(
        Long id,
        RemotePatientDTO patient,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        String status
) {
}
