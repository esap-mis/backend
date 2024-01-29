package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
