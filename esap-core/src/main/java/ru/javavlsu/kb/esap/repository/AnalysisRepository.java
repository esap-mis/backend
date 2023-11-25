package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.Analysis;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}
