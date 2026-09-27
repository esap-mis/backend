package ru.javavlsu.kb.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DoctorAppointmentDTO(
        Long id,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        PatientRefDTO patient
) {
}
