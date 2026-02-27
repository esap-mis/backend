package ru.javavlsu.kb.esap.dto;

import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.TimeSlotResponseDTO;
import ru.javavlsu.kb.esap.model.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record DoctorAppointmentDTO (
        Long id,
        LocalDate date,
        TimeSlotResponseDTO timeSlot,
        PatientDTO patient,
        AppointmentStatus status
) {}