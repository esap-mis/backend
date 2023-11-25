package ru.javavlsu.kb.esap.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentDTO {

    private Long patientId;

    private LocalDate date;

    private LocalTime startAppointments;

}
