package ru.javavlsu.kb.common.event;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Публикуется schedule-service при создании записи. Используется esap-core (AI-агент)
 * как источник истины без доступа к БД schedule-service.
 */
public record AppointmentCreatedEvent(
        Long appointmentId,
        Long scheduleId,
        Long patientId,
        Long doctorId,
        LocalDate date,
        LocalTime start,
        LocalTime end,
        String status
) {
}
