package ru.javavlsu.kb.esap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByClinic(Clinic clinic);

    int countPatientByClinic(Clinic clinic);

    Page<Patient> findAllByClinicOrderByIdDesc(Clinic clinic, Pageable pageable);

    @Query("SELECT p FROM Patient p " +
            "WHERE LOWER(p.firstName) " +
            "LIKE %:firstName% AND LOWER(p.patronymic) " +
            "LIKE %:patronymic% AND LOWER(p.lastName) " +
            "LIKE %:lastName% AND p.clinic = :clinic ORDER BY p.id ASC")
    Page<Patient> findAllByFullNameContainingIgnoreCaseAndClinicOrderByIdAsc(
            @Param("firstName") String firstName,
            @Param("patronymic") String patronymic,
            @Param("lastName") String lastName,
            @Param("clinic") Clinic clinic,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.gender = :gender AND p.clinic = :clinic")
    int getPatientsCountByGenderAndClinic(@Param("gender") int gender, @Param("clinic") Clinic clinic);

    @Query("SELECT COUNT(p) FROM Patient p WHERE FUNCTION('DATEDIFF', YEAR, p.birthDate, CURRENT_DATE) >= :minAge AND FUNCTION('DATEDIFF', YEAR, p.birthDate, CURRENT_DATE) <= :maxAge AND p.clinic = :clinic")
    int countPatientsByAgeRangeAndClinic(@Param("minAge") int minAge, @Param("maxAge") int maxAge, @Param("clinic") Clinic clinic);

    Optional<Patient> findByLogin(String login);
}
