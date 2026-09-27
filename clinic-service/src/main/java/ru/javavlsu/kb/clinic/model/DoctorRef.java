package ru.javavlsu.kb.clinic.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Локальная read-модель врача: нужна только для подписи медзаписи
 * (ФИО + специализация) без обращения к auth-service.
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "doctor_ref")
public class DoctorRef {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "clinic_id")
    private Long clinicId;

    public String getFullName() {
        return lastName + " " + firstName + " " + patronymic;
    }

    public String getFioAndSpecialization() {
        return specialization + ": " + getFullName();
    }
}
