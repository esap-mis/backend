package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponseDTO (
        Long id,
        PatientResponseDTO patient,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments
) {}
