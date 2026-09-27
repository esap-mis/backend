package ru.javavlsu.kb.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.clinic.model.MedicalCard;
import ru.javavlsu.kb.clinic.model.MedicalRecord;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByMedicalCardOrderByDateDesc(MedicalCard medicalCard);
}
