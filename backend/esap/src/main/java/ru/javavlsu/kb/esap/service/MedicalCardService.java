package ru.javavlsu.kb.esap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.MedicalRecord;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.repository.MedicalCardRepository;
import ru.javavlsu.kb.esap.repository.MedicalRecordRepository;
import ru.javavlsu.kb.esap.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class MedicalCardService {


    private final MedicalCardRepository medicalCardRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalCardService(MedicalCardRepository medicalCardRepository, MedicalRecordRepository medicalRecordRepository) {
        this.medicalCardRepository = medicalCardRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    //TODO Убрать метод или заменить (используется только для соранения medicalRecord)
    // заменен на getMedicalCardByPatientAndMedicalRecordSpecializationDoctor
    @Deprecated
    public MedicalCard getMedicalCardByPatient(Patient patient) throws NotFoundException {
        return medicalCardRepository.findByPatientOrderByMedicalRecordDateDesc(patient).orElseThrow(() -> new NotFoundException("Medical Record not found"));
    }

    public MedicalCard getMedicalCardByPatientAndMedicalRecordSpecializationDoctor(Patient patient, String specializationDoctor) throws NotFoundException {
        if(specializationDoctor != null && !specializationDoctor.isBlank()){
            return medicalCardRepository
                    .findMedicalCardByPatientAndMedicalRecordSpecializationDoctor(patient, specializationDoctor)
                    .orElseThrow(() -> new NotFoundException("Medical Record not found"));
        }
        return medicalCardRepository.findByPatientOrderByMedicalRecordDateDesc(patient)
                .orElseThrow(() -> new NotFoundException("Medical Record not found"));
    }

    @Transactional
    public void createMedicalRecord(MedicalRecord medicalRecord, MedicalCard medicalCard, Doctor doctor) {
        medicalRecord.setFioAndSpecializationDoctor(doctor.getSpecialization() + ": " + doctor.getFio());
        medicalRecord.setMedicalCard(medicalCard);
        medicalRecord.getAnalyzes().forEach(analysis -> analysis.setMedicalRecord(medicalRecord));
        medicalRecordRepository.save(medicalRecord);
    }


}
