package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.MedicalRecord;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByMedicalCardOrderByDateDesc(MedicalCard medicalCard);

}
