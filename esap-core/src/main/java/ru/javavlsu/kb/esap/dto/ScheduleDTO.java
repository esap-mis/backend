package ru.javavlsu.kb.esap.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ScheduleDTO {

    private Long doctorId;

    private LocalDate date;

    private LocalTime startDoctorAppointment;

    private LocalTime endDoctorAppointment;

}
