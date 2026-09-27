package ru.javavlsu.kb.common.event;

import java.time.LocalDate;

/**
 * Публикуется auth-service при создании/изменении пользователя.
 * clinic-service и schedule-service используют его для локальных read-моделей
 * (вместо синхронных запросов к auth-service на каждый чих).
 */
public record UserProfileEvent(
        Long userId,
        String login,
        String firstName,
        String patronymic,
        String lastName,
        String specialization,
        LocalDate birthDate,
        Integer gender,
        String address,
        String phoneNumber,
        String email,
        Long clinicId,
        String clinicName,
        String clinicAddress,
        String userType
) {
    public static final String TYPE_DOCTOR = "DOCTOR";
    public static final String TYPE_PATIENT = "PATIENT";
}
