package ru.javavlsu.kb.core.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record RemoteScheduleDTO(
        Long id,
        LocalDate date,
        LocalTime startDoctorAppointment,
        LocalTime endDoctorAppointment,
        int maxPatientPerDay,
        List<RemoteAppointmentDTO> appointments
) {
}
