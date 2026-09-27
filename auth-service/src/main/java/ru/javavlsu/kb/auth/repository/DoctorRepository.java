package ru.javavlsu.kb.auth.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByLogin(String login);

    @Query("SELECT d FROM Doctor d WHERE " +
            "LOWER(CONCAT(d.lastName, ' ', d.firstName, ' ', d.patronymic)) LIKE LOWER(CONCAT('%', :fullName, '%'))")
    List<Doctor> findByFullName(@Param("fullName") String fullName);

    @Query("SELECT d FROM Doctor d WHERE LOWER(d.specialization) = LOWER(:specialization)")
    List<Doctor> findBySpecialization(@Param("specialization") String specialization);

    Page<Doctor> findByClinicOrderByIdAsc(Clinic clinic, Pageable page);

    int countDoctorByClinic(Clinic clinic);
}
