package ru.javavlsu.kb.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.clinic.model.MedicalCard;
import ru.javavlsu.kb.clinic.model.PatientRef;

import java.util.Optional;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Long> {

    Optional<MedicalCard> findByPatientOrderByMedicalRecordDateDesc(PatientRef patient);

    Optional<MedicalCard> findByPatient(PatientRef patient);
}
