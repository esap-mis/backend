package ru.javavlsu.kb.schedule.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ScheduleResponseDTO(
        Long id,
        LocalDate date,
        LocalTime startDoctorAppointment,
        LocalTime endDoctorAppointment,
        int maxPatientPerDay,
        List<AppointmentResponseDTO> appointments
) {
}
