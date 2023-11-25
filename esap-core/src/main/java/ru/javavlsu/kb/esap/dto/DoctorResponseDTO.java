package ru.javavlsu.kb.esap.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.model.Role;

import java.util.Set;

@Getter
@Setter
public class DoctorResponseDTO {

    private Long id;

    private String firstName;

    @Size(max = 100)
    private String patronymic;

    private String lastName;

    private String specialization;

    private int gender;

    private ClinicDTO clinic;
}
