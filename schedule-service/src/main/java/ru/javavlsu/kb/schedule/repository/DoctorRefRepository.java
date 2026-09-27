package ru.javavlsu.kb.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.schedule.model.DoctorRef;

import java.util.List;

public interface DoctorRefRepository extends JpaRepository<DoctorRef, Long> {

    List<DoctorRef> findByFullNameContainingIgnoreCase(String fullName);

    List<DoctorRef> findBySpecializationIgnoreCase(String specialization);

    List<DoctorRef> findByClinicId(Long clinicId);
}
