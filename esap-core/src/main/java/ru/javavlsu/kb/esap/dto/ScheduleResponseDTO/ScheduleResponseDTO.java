package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record ScheduleResponseDTO (
        Long id,
        LocalDate date,
        int maxPatientPerDay,
        List<AppointmentResponseDTO> appointments,
        List<TimeSlotResponseDTO> timeSlots
) {}
