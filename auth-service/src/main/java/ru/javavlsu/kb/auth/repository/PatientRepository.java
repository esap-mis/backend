package ru.javavlsu.kb.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByClinic(Clinic clinic);

    int countPatientByClinic(Clinic clinic);

    Page<Patient> findAllByClinicOrderByIdDesc(Clinic clinic, Pageable pageable);

    @Query("SELECT p FROM Patient p " +
            "WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
            "AND LOWER(p.patronymic) LIKE LOWER(CONCAT('%', :patronymic, '%')) " +
            "AND LOWER(p.lastName) LIKE LOWER(CONCAT('%', :lastName, '%')) " +
            "AND p.clinic = :clinic ORDER BY p.id ASC")
    Page<Patient> findAllByFullNameContainingIgnoreCaseAndClinicOrderByIdAsc(
            @Param("firstName") String firstName,
            @Param("patronymic") String patronymic,
            @Param("lastName") String lastName,
            @Param("clinic") Clinic clinic,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.gender = :gender AND p.clinic = :clinic")
    int getPatientsCountByGenderAndClinic(@Param("gender") int gender, @Param("clinic") Clinic clinic);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.birthDate <= :minDate AND p.birthDate > :maxDate AND p.clinic = :clinic")
    int countPatientsByAgeRangeAndClinic(@Param("minDate") java.time.LocalDate minDate,
                                         @Param("maxDate") java.time.LocalDate maxDate,
                                         @Param("clinic") Clinic clinic);

    Optional<Patient> findByLogin(String login);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(CONCAT(p.lastName, ' ', p.firstName, ' ', p.patronymic)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Patient> findByFullName(@Param("fullName") String fullName);
}
