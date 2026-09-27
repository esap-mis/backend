package ru.javavlsu.kb.clinic.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Локальная read-модель пациента в clinic-service.
 * Наполняется из UserProfileEvent, чтобы не ходить в auth-service за ФИО.
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "patient_ref")
public class PatientRef {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "clinic_id")
    private Long clinicId;

    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }
}
