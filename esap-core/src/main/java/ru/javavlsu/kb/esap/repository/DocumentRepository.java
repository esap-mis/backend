package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.Document;
import ru.javavlsu.kb.esap.model.MedicalRecord;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findDocumentByKey(String key);

    List<Document> findDocumentsByMedicalRecord(MedicalRecord medicalRecord);
}
