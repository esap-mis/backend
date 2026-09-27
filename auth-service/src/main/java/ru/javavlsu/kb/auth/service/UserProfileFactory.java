package ru.javavlsu.kb.auth.service;

import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.model.Doctor;
import ru.javavlsu.kb.auth.model.Patient;
import ru.javavlsu.kb.common.event.UserProfileEvent;

/**
 * Собирает UserProfileEvent из локальных сущностей. Единственное место,
 * где формат события identity привязан к модели auth-service.
 */
final class UserProfileFactory {

    private UserProfileFactory() {
    }

    static UserProfileEvent from(Patient patient, Clinic clinic) {
        return new UserProfileEvent(
                patient.getId(),
                patient.getLogin(),
                patient.getFirstName(),
                patient.getPatronymic(),
                patient.getLastName(),
                null,
                patient.getBirthDate(),
                patient.getGender(),
                patient.getAddress(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                clinic != null ? clinic.getId() : null,
                clinic != null ? clinic.getName() : null,
                clinic != null ? clinic.getAddress() : null,
                UserProfileEvent.TYPE_PATIENT
        );
    }

    static UserProfileEvent from(Doctor doctor, Clinic clinic) {
        return new UserProfileEvent(
                doctor.getId(),
                doctor.getLogin(),
                doctor.getFirstName(),
                doctor.getPatronymic(),
                doctor.getLastName(),
                doctor.getSpecialization(),
                null,
                doctor.getGender(),
                null,
                null,
                null,
                clinic != null ? clinic.getId() : null,
                clinic != null ? clinic.getName() : null,
                clinic != null ? clinic.getAddress() : null,
                UserProfileEvent.TYPE_DOCTOR
        );
    }
}
