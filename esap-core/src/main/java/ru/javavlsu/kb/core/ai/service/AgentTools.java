package ru.javavlsu.kb.core.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.core.client.EsapClient;
import ru.javavlsu.kb.core.dto.AppointmentRequestDTO;
import ru.javavlsu.kb.core.dto.CurrentDateTime;
import ru.javavlsu.kb.core.dto.RemoteDoctorDTO;
import ru.javavlsu.kb.core.dto.RemoteMedicalCardDTO;
import ru.javavlsu.kb.core.dto.RemotePatientDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Инструменты AI-агента. Все обращения к данным идут через EsapClient,
 * поэтому агент не знает про базы других сервисов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentTools {

    private final EsapClient esapClient;
    private final CurrentUser currentUser;

    @Tool(description = "Получить историю болезни (медицинскую карту) текущего авторизованного пациента")
    public String getMyMedicalHistory() {
        try {
            if (!currentUser.isPatient()) {
                return "Вы не авторизованы как пациент. Пожалуйста, войдите в систему.";
            }
            RemoteMedicalCardDTO card = esapClient.getMedicalCard(currentUser.id());
            if (card == null || card.medicalRecord() == null || card.medicalRecord().isEmpty()) {
                return "Ваша медицинская карта пуста.";
            }
            return "История болезни пациента " + currentUser.id() + ": " + card.medicalRecord();
        } catch (Exception e) {
            log.error("Error getting current patient medical history", e);
            return "Произошла ошибка при получении медицинской карты: " + e.getMessage();
        }
    }

    @Tool(description = "Получить список моих предстоящих записей на приём (включая отменённые)")
    public String getMyUpcomingAppointments() {
        try {
            if (!currentUser.isPatient()) {
                return "Вы не авторизованы как пациент.";
            }
            List<?> appointments = esapClient.getUpcomingAppointments();
            if (appointments.isEmpty()) {
                return "У вас нет записей на прием.";
            }
            return "Ваши записи (CONFIRMED - активна, CANCELLED - отменена): " + appointments;
        } catch (Exception e) {
            log.error("Error getting current patient upcoming appointments", e);
            return "Произошла ошибка при получении ваших записей: " + e.getMessage();
        }
    }

    @Tool(description = "Записаться на прием к врачу (для текущего авторизованного пациента)")
    public String bookMyselfForAppointment(@ToolParam(description = "ID расписания врача") Long scheduleId,
                                          @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date,
                                          @ToolParam(description = "Время начала в формате ЧЧ:ММ") String time) {
        try {
            if (!currentUser.isPatient()) {
                return "Вы не авторизованы как пациент.";
            }
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            return "Запись успешно создана: " +
                    esapClient.createAppointment(scheduleId, new AppointmentRequestDTO(currentUser.id(), localDate, localTime));
        } catch (Exception e) {
            log.error("Error booking appointment for current user", e);
            return "Не удалось создать запись: " + e.getMessage();
        }
    }

    @Tool(description = "Найти доступные мета для записи к врачу по фамилии, имени и отчеству и дате")
    public String findAvailableAppointmentsByFullName(@ToolParam(description = "Фамилия, имя или отчество врача") String fullName,
                                                      @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        try {
            LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<LocalTime> available = esapClient.findDoctorsByFullName(fullName).stream()
                    .flatMap(doctor -> esapClient.getAvailableAppointments(doctor.id(), searchDate).stream())
                    .toList();
            return available.isEmpty() ? "Свободных слотов не найдено." : available.toString();
        } catch (Exception e) {
            log.error("Error finding available appointments by full name", e);
            return "Произошла ошибка при поиске доступных слотов: " + e.getMessage();
        }
    }

    @Tool(description = "Найти доступные мета для записи к врачу по специальности и дате")
    public String findAvailableAppointmentsBySpec(@ToolParam(description = "Специальность врача") String specialization,
                                                  @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        try {
            LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<LocalTime> available = esapClient.findDoctorsBySpecialization(specialization).stream()
                    .flatMap(doctor -> esapClient.getAvailableAppointments(doctor.id(), searchDate).stream())
                    .toList();
            return available.isEmpty() ? "Свободных слотов не найдено." : available.toString();
        } catch (Exception e) {
            log.error("Error finding available appointments by specialization", e);
            return "Произошла ошибка при поиске доступных слотов: " + e.getMessage();
        }
    }

    @Tool(description = "Найти врача по ФИО")
    public String findDoctorByFullName(@ToolParam(description = """
            Полное имя врача.
            ВАЖНО: сначала указывайте фамилию, затем имя и отчество в качестве строки.
            Например: "Иванов Иван Иванович" или "Петрова Анна"
            """) String fullName) {
        try {
            List<RemoteDoctorDTO> doctors = esapClient.findDoctorsByFullName(fullName);
            return doctors.isEmpty() ? "Врачи не найдены." : doctors.toString();
        } catch (Exception e) {
            log.error("Error finding doctor by full name", e);
            return "Произошла ошибка при поиске врачей: " + e.getMessage();
        }
    }

    @Tool(description = "Найти врача по специализации")
    public String findDoctorBySpecialization(@ToolParam(description = "Специальность врача") String specialization) {
        try {
            List<RemoteDoctorDTO> doctors = esapClient.findDoctorsBySpecialization(specialization);
            return doctors.isEmpty() ? "Врачи по данной специальности не найдены." : doctors.toString();
        } catch (Exception e) {
            log.error("Error finding doctor by specialization", e);
            return "Произошла ошибка при поиске врачей: " + e.getMessage();
        }
    }

    @Tool(description = "Записать пациента на прием к врачу. Используйте, когда пациент уже передал вам ID пациента, желаемые дату и время в названии")
    public String createAppointment(@ToolParam(description = "ID расписания врача") Long scheduleId,
                                    @ToolParam(description = "Детали записи: ID пациента, желаемые дату и время в формате") AppointmentRequestDTO appointmentDTO) {
        try {
            return "Запись успешно создана: " + esapClient.createAppointment(scheduleId, appointmentDTO);
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            return "Не удалось создать запись: " + e.getMessage();
        }
    }

    @Tool(description = "Отменить существующую запись на прием. Используйте, когда пациент просит отменить запись")
    public String cancelAppointment(@ToolParam(description = "ID записи") Long appointmentId) {
        try {
            esapClient.cancelAppointment(appointmentId);
            return "Запись #" + appointmentId + " успешно отменена (статус изменён на CANCELLED).";
        } catch (Exception e) {
            log.error("Error cancelling appointment", e);
            return "Произошла ошибка при отмене записи: " + e.getMessage();
        }
    }

    @Tool(description = "Получить историю болезни (медицинскую карту) пациента по его ID")
    public String getPatientMedicalHistory(@ToolParam(description = "ID пациента") Long patientId) {
        try {
            RemoteMedicalCardDTO card = esapClient.getMedicalCard(patientId);
            if (card == null || card.medicalRecord() == null || card.medicalRecord().isEmpty()) {
                return "Медицинская карта пуста или не найдена.";
            }
            return "История болезни пациента " + patientId + ": " + card.medicalRecord();
        } catch (Exception e) {
            log.error("Error getting patient medical history", e);
            return "Произошла ошибка при получении медицинской карты: " + e.getMessage();
        }
    }

    @Tool(description = "Получить расписание конкретного врача на конкретную дату")
    public String getDoctorSchedule(@ToolParam(description = "ID врача") Long doctorId,
                                    @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        try {
            LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            List<LocalTime> available = esapClient.getAvailableAppointments(doctorId, scheduleDate);
            return available.isEmpty()
                    ? "Расписание не найдено на указанную дату."
                    : "Доступное время для записи: " + available;
        } catch (Exception e) {
            log.error("Error getting doctor schedule", e);
            return "Произошла ошибка при получении расписания врача: " + e.getMessage();
        }
    }

    @Tool(description = "Найти пациента по ФИО")
    public String findPatientByFullName(@ToolParam(description = """
            Полное имя пациента.
            ВАЖНО: сначала указывайте фамилию, затем имя и отчество в качестве строки.
            Например: "Иванов Иван Иванович" или "Петрова Анна"
            """) String fullName) {
        try {
            List<RemotePatientDTO> patients = esapClient.findPatientsByFullName(fullName);
            return patients.isEmpty() ? "Пациенты не найдены." : patients.toString();
        } catch (Exception e) {
            log.error("Error finding patient by full name", e);
            return "Произошла ошибка при поиске пациентов: " + e.getMessage();
        }
    }

    @Tool(description = "Получить текущую дату и время. Используй для определения 'сегодня', 'завтра', 'сейчас'")
    public String getCurrentDateTime() {
        try {
            LocalDateTime now = LocalDateTime.now();
            return new CurrentDateTime(
                    now.format(DateTimeFormatter.ISO_DATE),
                    now.format(DateTimeFormatter.ofPattern("HH:mm")),
                    now.getDayOfWeek().toString(),
                    now).toString();
        } catch (Exception e) {
            log.error("Error getting current date time", e);
            return "Ошибка при получении текущего времени: " + e.getMessage();
        }
    }
}
