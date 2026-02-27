package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public record TimeSlotResponseDTO (
    Long id,
    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime,
    @JsonFormat(pattern = "HH:mm")
    LocalTime endTime,
    Boolean isAvailable
) {}
