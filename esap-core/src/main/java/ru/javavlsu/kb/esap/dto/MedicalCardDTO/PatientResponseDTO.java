package ru.javavlsu.kb.esap.dto.MedicalCardDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class PatientResponseDTO {

    private String firstName;

    private String patronymic;

    private String lastName;

    private LocalDate birthDate;

    private int gender;

    private String phoneNumber;

    private String address;

}
