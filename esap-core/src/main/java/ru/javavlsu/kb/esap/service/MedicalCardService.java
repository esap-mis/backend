package ru.javavlsu.kb.esap.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.dto.notifications.NotificationEvent;
import ru.javavlsu.kb.esap.kafka.KafkaProducer;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.MedicalRecord;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.repository.MedicalCardRepository;
import ru.javavlsu.kb.esap.repository.MedicalRecordRepository;
import ru.javavlsu.kb.esap.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MedicalCardService {
    public static final String ADD_MEDICAL_RECORD_REMINDER_TITLE = "Время проверить медицинскую карту";
    public static final String ADD_MEDICAL_RECORD_REMINDER_BODY_TEMPLATE = "%s добавил новую запись в вашу медицинскую карту. Пожалуйста, проверьте результаты.";
    private final MedicalCardRepository medicalCardRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final KafkaProducer kafkaProducer;

    private static final String DEFAULT_ANALYSIS_RESULT = "Не готов";

    public MedicalCardService(MedicalCardRepository medicalCardRepository, MedicalRecordRepository medicalRecordRepository, KafkaProducer kafkaProducer) {
        this.medicalCardRepository = medicalCardRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.kafkaProducer = kafkaProducer;
    }

    //TODO Убрать метод или заменить (используется только для соранения medicalRecord)
    // заменен на getMedicalCardByPatientAndMedicalRecordSpecializationDoctor
    @Deprecated
    public MedicalCard getMedicalCardByPatient(Patient patient) throws NotFoundException {
        return medicalCardRepository.findByPatientOrderByMedicalRecordDateDesc(patient).orElseThrow(() -> new NotFoundException("Medical Card not found"));
    }

    public List<MedicalRecord> getMedicalRecordByMedicalCard(MedicalCard medicalCard) throws NotFoundException {
        return medicalRecordRepository.findByMedicalCardOrderByDateDesc(medicalCard);
    }

    public MedicalCard getMedicalCardByPatientAndMedicalRecordSpecializationDoctor(Patient patient, String specializationDoctor) throws NotFoundException {
        if(specializationDoctor != null && !specializationDoctor.isBlank()){
            return medicalCardRepository
                    .findMedicalCardByPatientAndMedicalRecordSpecializationDoctor(patient, specializationDoctor)
                    .orElseThrow(() -> new NotFoundException("Medical Card not found"));
        }
        return medicalCardRepository.findByPatientOrderByMedicalRecordDateDesc(patient)
                .orElseThrow(() -> new NotFoundException("Medical Card not found"));
    }

    @Transactional
    public void createMedicalRecord(MedicalRecord medicalRecord, MedicalCard medicalCard, Doctor doctor) throws JsonProcessingException {
        medicalRecord.setFioAndSpecializationDoctor(doctor.getSpecialization() + ": " + doctor.getFullName());
        medicalRecord.setMedicalCard(medicalCard);
        medicalRecord.getAnalyzes().forEach(analysis -> analysis.setMedicalRecord(medicalRecord));
        medicalRecord.getAnalyzes().forEach(analysis -> {
            analysis.setMedicalRecord(medicalRecord);
            analysis.setResult(DEFAULT_ANALYSIS_RESULT);
            analysis.setDate(LocalDateTime.now().withNano(0));
        });
        MedicalRecord record = medicalRecordRepository.save(medicalRecord);
        sendMedicalRecordAddReminder(record, doctor);
    }

    private void sendMedicalRecordAddReminder(MedicalRecord medicalRecord, Doctor doctor) throws JsonProcessingException {
        final NotificationEvent notificationEvent = new NotificationEvent(
                medicalRecord.getMedicalCard().getPatient().getId(),
                ADD_MEDICAL_RECORD_REMINDER_TITLE,
                String.format(ADD_MEDICAL_RECORD_REMINDER_BODY_TEMPLATE,
                        doctor.getFullName())
        );
        kafkaProducer.sendNotificationEvent(notificationEvent);
    }
}
