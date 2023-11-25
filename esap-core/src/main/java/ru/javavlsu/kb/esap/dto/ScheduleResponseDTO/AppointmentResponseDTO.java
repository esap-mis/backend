package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentResponseDTO {

    private Long id;

    private PatientResponseDTO patient;

    private LocalDate date;

    private LocalTime startAppointments;

    private LocalTime endAppointments;


}
