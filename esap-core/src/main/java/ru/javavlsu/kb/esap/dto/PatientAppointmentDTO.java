package ru.javavlsu.kb.esap.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record PatientAppointmentDTO (
        Long id,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        DoctorResponseDTO doctor
) {}