package ru.javavlsu.kb.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.auth.model.Clinic;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
}
