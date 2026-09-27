package ru.javavlsu.kb.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.clinic.model.PatientRef;

import java.util.List;

public interface PatientRefRepository extends JpaRepository<PatientRef, Long> {

    List<PatientRef> findByFullNameContainingIgnoreCase(String fullName);
}
