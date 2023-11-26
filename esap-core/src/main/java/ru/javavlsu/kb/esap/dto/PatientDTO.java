package ru.javavlsu.kb.esap.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.model.Clinic;

import java.time.LocalDate;

@Getter
@Setter
public class PatientDTO {

    private Long id;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String patronymic;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotNull
    private LocalDate birthDate;

    @Max(value = 2, message = "Не верно указан пол")
    @Min(value = 1, message = "Не верно указан пол")
    private int gender;

    @Size(max = 200)
    private String address;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    private Clinic clinic;

}
