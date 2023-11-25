package ru.javavlsu.kb.esap.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PatientAppointmentDTO {

    private Long id;

    private LocalDate date;

    private LocalTime startAppointments;

    private LocalTime endAppointments;

    private DoctorResponseDTO doctor;
}