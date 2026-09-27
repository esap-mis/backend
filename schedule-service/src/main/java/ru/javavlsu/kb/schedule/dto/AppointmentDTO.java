package ru.javavlsu.kb.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDTO(
        Long patientId,
        LocalDate date,
        LocalTime startAppointments
) {
}
