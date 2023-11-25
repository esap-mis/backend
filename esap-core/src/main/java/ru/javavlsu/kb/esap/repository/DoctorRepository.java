package ru.javavlsu.kb.esap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.Doctor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByLogin(String login);
    Page<Doctor> findByClinicOrderByIdAsc(Clinic clinic, Pageable page);
    @Query("SELECT d FROM Doctor d JOIN FETCH d.schedules s WHERE s.date = :date AND d.clinic = :clinic ORDER BY d.id ASC")
    List<Doctor> findByClinicAndSchedulesDateOrderByIdAsc(@Param("clinic") Clinic clinic, @Param("date") LocalDate date);
    int countDoctorByClinic(Clinic clinic);
}
