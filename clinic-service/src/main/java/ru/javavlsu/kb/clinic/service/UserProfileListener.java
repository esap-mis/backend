package ru.javavlsu.kb.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.clinic.model.DoctorRef;
import ru.javavlsu.kb.clinic.model.PatientRef;
import ru.javavlsu.kb.clinic.repository.DoctorRefRepository;
import ru.javavlsu.kb.clinic.repository.PatientRefRepository;
import ru.javavlsu.kb.common.event.UserProfileEvent;

/**
 * Наполняет локальные read-модели пациентов и врачей из событий auth-service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileListener {

    private final PatientRefRepository patientRefRepository;
    private final DoctorRefRepository doctorRefRepository;

    @KafkaListener(topics = "${esap.kafka.topics.user-profile}", groupId = "clinic-service")
    @Transactional
    public void onUserProfile(UserProfileEvent event) {
        if (UserProfileEvent.TYPE_PATIENT.equals(event.userType())) {
            PatientRef ref = patientRefRepository.findById(event.userId()).orElseGet(PatientRef::new);
            ref.setId(event.userId());
            ref.setFirstName(event.firstName());
            ref.setPatronymic(event.patronymic());
            ref.setLastName(event.lastName());
            ref.setBirthDate(event.birthDate());
            ref.setGender(event.gender());
            ref.setAddress(event.address());
            ref.setPhoneNumber(event.phoneNumber());
            ref.setEmail(event.email());
            ref.setClinicId(event.clinicId());
            patientRefRepository.save(ref);
            log.debug("Updated patient read-model {}", event.userId());
        } else if (UserProfileEvent.TYPE_DOCTOR.equals(event.userType())) {
            DoctorRef ref = doctorRefRepository.findById(event.userId()).orElseGet(DoctorRef::new);
            ref.setId(event.userId());
            ref.setFirstName(event.firstName());
            ref.setPatronymic(event.patronymic());
            ref.setLastName(event.lastName());
            ref.setSpecialization(event.specialization());
            ref.setGender(event.gender());
            ref.setClinicId(event.clinicId());
            doctorRefRepository.save(ref);
            log.debug("Updated doctor read-model {}", event.userId());
        }
    }
}
