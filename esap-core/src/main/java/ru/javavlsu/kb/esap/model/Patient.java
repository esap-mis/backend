package ru.javavlsu.kb.esap.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@Table(name = "patients")
public class Patient extends User {

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

    @Size(max = 200)
    private String address;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @OneToOne(mappedBy = "patient", fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    private MedicalCard medicalCard;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

}
