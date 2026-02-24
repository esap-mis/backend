package ru.javavlsu.kb.esap.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DoctorAppointmentDTO (
        Long id,
        LocalDate date,
        LocalTime startAppointments,
        LocalTime endAppointments,
        PatientDTO patient
) {}