package ru.javavlsu.kb.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.clinic.model.DoctorRef;

import java.util.List;

public interface DoctorRefRepository extends JpaRepository<DoctorRef, Long> {

    List<DoctorRef> findByFullNameContainingIgnoreCase(String fullName);

    List<DoctorRef> findBySpecializationIgnoreCase(String specialization);
}
