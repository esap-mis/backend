package ru.javavlsu.kb.esap.dto.ScheduleResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientResponseDTO {

    private Long id;

    private String firstName;

    private String patronymic;

    private String lastName;

    private LocalDate birthDate;

    private int gender;

    private String address;

    private String phoneNumber;

    private String email;
}
