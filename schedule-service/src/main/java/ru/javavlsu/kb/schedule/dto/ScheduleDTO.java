package ru.javavlsu.kb.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ScheduleDTO(
        Long doctorId,
        LocalDate date,
        LocalTime startDoctorAppointment,
        LocalTime endDoctorAppointment
) {
}
