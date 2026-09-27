package ru.javavlsu.kb.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.schedule.model.PatientRef;

import java.util.List;

public interface PatientRefRepository extends JpaRepository<PatientRef, Long> {

    List<PatientRef> findByFullNameContainingIgnoreCase(String fullName);
}
