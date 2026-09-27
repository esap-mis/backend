package ru.javavlsu.kb.clinic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.clinic.dto.MedicalCardResponseDTO;
import ru.javavlsu.kb.clinic.dto.MedicalRecordRequestDTO;
import ru.javavlsu.kb.clinic.dto.MedicalRecordResponseDTO;
import ru.javavlsu.kb.clinic.mapper.MedicalCardMapper;
import ru.javavlsu.kb.clinic.model.DoctorRef;
import ru.javavlsu.kb.clinic.model.MedicalCard;
import ru.javavlsu.kb.clinic.model.MedicalRecord;
import ru.javavlsu.kb.clinic.model.PatientRef;
import ru.javavlsu.kb.clinic.repository.DoctorRefRepository;
import ru.javavlsu.kb.clinic.repository.MedicalCardRepository;
import ru.javavlsu.kb.clinic.repository.MedicalRecordRepository;
import ru.javavlsu.kb.clinic.repository.PatientRefRepository;
import ru.javavlsu.kb.common.event.NotificationEvent;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicalCardService {

    public static final String ADD_MEDICAL_RECORD_REMINDER_TITLE = "Врач провёл медицинскую карту";
    public static final String ADD_MEDICAL_RECORD_REMINDER_BODY_TEMPLATE =
            "%s добавил новую запись в вашу медицинскую карту. Пожалуйста, проверьте результаты.";
    private static final String DEFAULT_ANALYSIS_RESULT = "Не готов";

    private final MedicalCardRepository medicalCardRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRefRepository patientRefRepository;
    private final DoctorRefRepository doctorRefRepository;
    private final MedicalCardMapper medicalCardMapper;
    private final EventPublisher eventPublisher;

    public MedicalCard getMedicalCardByPatient(Long patientId) {
        return medicalCardRepository.findByPatientOrderByMedicalRecordDateDesc(requirePatient(patientId))
                .orElseThrow(() -> new NotFoundException("Medical Card not found"));
    }

    public List<MedicalRecord> getMedicalRecordByMedicalCard(MedicalCard medicalCard) {
        return medicalRecordRepository.findByMedicalCardOrderByDateDesc(medicalCard);
    }

    /**
     * Фильтрация по специализации делается на DTO, а не на lazy-коллекции сущности:
     * мутация persistence-контекста ломала бы последующие выгрузки в той же сессии.
     */
    public MedicalCardResponseDTO getMedicalCardResponse(Long patientId, String specializationDoctor) {
        MedicalCard card = getMedicalCardByPatient(patientId);
        List<MedicalRecord> records = getMedicalRecordByMedicalCard(card);
        if (specializationDoctor != null && !specializationDoctor.isBlank()) {
            records = records.stream()
                    .filter(r -> r.getFioAndSpecializationDoctor() != null
                            && r.getFioAndSpecializationDoctor().toLowerCase().contains(specializationDoctor.toLowerCase()))
                    .toList();
        }
        return new MedicalCardResponseDTO(
                card.getId(),
                medicalCardMapper.toMedicalRecordResponseList(records),
                medicalCardMapper.toPatientRefDTO(card.getPatient()));
    }

    @Transactional
    public void createMedicalRecord(MedicalRecordRequestDTO dto, Long patientId, Long doctorId)
            throws JsonProcessingException {
        DoctorRef doctor = doctorRefRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        MedicalCard medicalCard = getMedicalCardByPatient(patientId);
        MedicalRecord medicalRecord = medicalCardMapper.toMedicalRecord(dto);
        medicalRecord.setFioAndSpecializationDoctor(doctor.getFioAndSpecialization());
        medicalRecord.setMedicalCard(medicalCard);
        if (medicalRecord.getDate() == null) {
            medicalRecord.setDate(LocalDate.now());
        }
        if (medicalRecord.getAnalyzes() != null) {
            medicalRecord.getAnalyzes().forEach(analysis -> {
                analysis.setMedicalRecord(medicalRecord);
                analysis.setResult(DEFAULT_ANALYSIS_RESULT);
                analysis.setDate(LocalDateTime.now().withNano(0));
            });
        }
        MedicalRecord saved = medicalRecordRepository.save(medicalRecord);
        sendMedicalRecordAddReminder(saved, doctor);
    }

    private void sendMedicalRecordAddReminder(MedicalRecord medicalRecord, DoctorRef doctor) {
        eventPublisher.sendNotificationEvent(new NotificationEvent(
                medicalRecord.getMedicalCard().getPatient().getId(),
                ADD_MEDICAL_RECORD_REMINDER_TITLE,
                String.format(ADD_MEDICAL_RECORD_REMINDER_BODY_TEMPLATE, doctor.getFullName())));
    }

    private PatientRef requirePatient(Long patientId) {
        return patientRefRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }
}
