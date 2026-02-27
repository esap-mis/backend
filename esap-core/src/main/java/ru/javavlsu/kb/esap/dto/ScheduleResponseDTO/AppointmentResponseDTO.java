package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import ru.javavlsu.kb.esap.model.AppointmentStatus;
import java.time.LocalDate;

public record AppointmentResponseDTO (
        Long id,
        LocalDate date,
        PatientResponseDTO patient,
        AppointmentStatus status,
        TimeSlotResponseDTO timeSlot
) {}
