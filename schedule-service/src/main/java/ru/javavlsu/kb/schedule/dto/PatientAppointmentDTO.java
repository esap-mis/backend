package ru.javavlsu.kb.schedule.dto;

import ru.javavlsu.kb.schedule.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record PatientAppointmentDTO(
        Long id,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        DoctorRefDTO doctor,
        AppointmentStatus status
) {
}
