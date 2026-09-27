package ru.javavlsu.kb.common.event;

/**
 * Публикуется auth-service после регистрации пациента.
 * notification-service отправляет письмо с логином и паролем.
 */
public record PatientCreatedEvent(
        Long patientId,
        String email,
        String firstName,
        String login,
        String password,
        String clinicName
) {
}
