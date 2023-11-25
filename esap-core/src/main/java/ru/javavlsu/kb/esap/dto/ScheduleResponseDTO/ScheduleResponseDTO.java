package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.dto.DoctorDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class ScheduleResponseDTO {

    private Long id;

    private LocalDate date;

    private LocalTime startDoctorAppointment;

    private LocalTime endDoctorAppointment;

    private int maxPatientPerDay;

    private List<AppointmentResponseDTO> appointments;

}
