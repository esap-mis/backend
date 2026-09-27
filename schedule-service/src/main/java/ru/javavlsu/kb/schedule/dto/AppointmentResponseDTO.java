package ru.javavlsu.kb.schedule.dto;

import ru.javavlsu.kb.schedule.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponseDTO(
        Long id,
        PatientRefDTO patient,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        AppointmentStatus status
) {
}
