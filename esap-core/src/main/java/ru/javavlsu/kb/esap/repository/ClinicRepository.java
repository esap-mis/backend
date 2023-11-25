package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.Doctor;

import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {

//    Optional<Doctor> findByLogin(String login);

}
