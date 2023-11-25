package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.Patient;

import java.util.Optional;

public interface MedicalCardRepository extends JpaRepository<MedicalCard, Long> {


    Optional<MedicalCard> findByPatientOrderByMedicalRecordDateDesc(Patient patient);

    @Query("SELECT mc FROM MedicalCard mc " +
            "JOIN FETCH mc.medicalRecord mr " +
            "WHERE mc.patient = :patient " +
            "AND LOWER(mr.fioAndSpecializationDoctor) LIKE CONCAT('%%', :specializationDoctor, '%%') " +
            "ORDER BY mr.date DESC")
    Optional<MedicalCard> findMedicalCardByPatientAndMedicalRecordSpecializationDoctor(@Param("patient") Patient patient,
                                                                                       @Param("specializationDoctor") String specialization);

}
