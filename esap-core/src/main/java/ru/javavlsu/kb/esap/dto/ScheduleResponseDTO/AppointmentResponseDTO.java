package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import ru.javavlsu.kb.esap.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponseDTO (
        Long id,
        PatientResponseDTO patient,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        AppointmentStatus status
) {}
