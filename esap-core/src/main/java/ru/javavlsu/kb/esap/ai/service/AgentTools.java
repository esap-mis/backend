package ru.javavlsu.kb.esap.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import ru.javavlsu.kb.esap.dto.AppointmentDTO;
import ru.javavlsu.kb.esap.dto.CurrentDateTime;
import ru.javavlsu.kb.esap.dto.DoctorResponseDTO;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalCardResponseDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.AppointmentResponseDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.PatientResponseDTO;
import ru.javavlsu.kb.esap.mapper.MedicalCardMapper;
import ru.javavlsu.kb.esap.mapper.ScheduleMapper;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.model.Schedule;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.service.*;

import ru.javavlsu.kb.esap.util.UserUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class AgentTools {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final ScheduleMapper scheduleMapper;
    private final MedicalCardService medicalCardService;
    private final MedicalCardMapper medicalCardMapper;
    private final UserUtils userUtils;

    public AgentTools(DoctorService doctorService, PatientService patientService, ScheduleService scheduleService, AppointmentService appointmentService, ScheduleMapper scheduleMapper, MedicalCardService medicalCardService, MedicalCardMapper medicalCardMapper, UserUtils userUtils) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
        this.scheduleMapper = scheduleMapper;
        this.medicalCardService = medicalCardService;
        this.medicalCardMapper = medicalCardMapper;
        this.userUtils = userUtils;
    }

    @Tool(description = "Получить историю болезни (медицинскую карту) текущего авторизованного пациента")
    public String getMyMedicalHistory() {
        log.info("Get medical history for current user");
        try {
            final User user = userUtils.UserDetails().getUser();
            if (!(user instanceof Patient patient)) {
                return "Вы не авторизованы как пациент. Пожалуйста, войдите в систему.";
            }
            final MedicalCardResponseDTO medicalCard = medicalCardMapper.toMedicalCard(medicalCardService.getMedicalCardByPatient(patient));
            if (medicalCard == null || medicalCard.getMedicalRecord() == null || medicalCard.getMedicalRecord().isEmpty()) {
                return "Ваша медицинская карта пуста.";
            }
            return "История болезни пациента " + patient.getLastName() + " " + patient.getFirstName() + ": " + medicalCard.getMedicalRecord();
        } catch (Exception e) {
            log.error("Error getting current patient medical history", e);
            return "Произошла ошибка при получении вашей медицинской карты: " + e.getMessage();
        }
    }

    @Tool(description = "Получить список моих предстоящих записей на прием")
    public String getMyUpcomingAppointments() {
        log.info("Get upcoming appointments for current user");
        try {
            final User user = userUtils.UserDetails().getUser();
            if (!(user instanceof Patient patient)) {
                return "Вы не авторизованы как пациент.";
            }
            final List<ru.javavlsu.kb.esap.dto.PatientAppointmentDTO> appointments = appointmentService.getAppointmentsForUser(patient);
            if (appointments.isEmpty()) {
                return "У вас нет записей на прием.";
            }
            return "Ваши записи: " + appointments;
        } catch (Exception e) {
            log.error("Error getting current patient upcoming appointments", e);
            return "Произошла ошибка при получении ваших записей: " + e.getMessage();
        }
    }

    @Tool(description = "Записаться на прием к врачу (для текущего авторизованного пациента)")
    public String bookMyselfForAppointment(@ToolParam(description = "ID расписания врача") Long scheduleId,
                                           @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date,
                                           @ToolParam(description = "Время начала в формате ЧЧ:ММ (например, 09:00 или 14:30)") String time) {
        log.info("Book appointment for current user: scheduleId={}, date={}, time={}", scheduleId, date, time);
        try {
            ru.javavlsu.kb.esap.model.User user = userUtils.UserDetails().getUser();
            if (!(user instanceof Patient patient)) {
                return "Вы не авторизованы как пациент.";
            }
            final LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            final LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            
            final AppointmentDTO appointmentDTO = new AppointmentDTO(patient.getId(), localDate, localTime);
            final AppointmentResponseDTO response = appointmentService.create(appointmentDTO, scheduleId);
            return "Запись успешно создана: " + response.toString();
        } catch (Exception e) {
            log.error("Error booking appointment for current user", e);
            return "Не удалось создать запись: " + e.getMessage();
        }
    }

    @Tool(description = "Найти доступные места для записи к врачу по специальности и дате")
    public String findAvailableAppointmentsByFullName(@ToolParam(description = "Фамилия, имя или отчество врача") String fullName,
                                                     @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        log.info("Find available times for new appointment: fullName={}, date={}", fullName, date);
        try {
            final LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            final List<DoctorResponseDTO> doctors = doctorService.findByFullName(fullName);
            final List<LocalTime> availableTimes = doctors.stream()
                    .flatMap(doctor -> appointmentService.findAvailableAppointments(doctor.id(), searchDate).stream())
                    .toList();
            return availableTimes.isEmpty() ? "Свободных слотов не найдено." : availableTimes.toString();
        } catch (Exception e) {
            log.error("Error finding available appointments by full name", e);
            return "Произошла ошибка при поиске доступных слотов: " + e.getMessage();
        }
    }

    @Tool(description = "Найти доступные места для записи к врачу по специальности и дате")
    public String findAvailableAppointmentsBySpec(@ToolParam(description = "Специальность врача (например: терапевт, хирург, окулист)") String specialization,
                                                     @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        log.info("Find available times for new appointment: specialization={}, date={}", specialization, date);
        try {
            final LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            final List<DoctorResponseDTO> doctors = doctorService.findBySpecialization(specialization);
            final List<LocalTime> availableTimes = doctors.stream()
                    .flatMap(doctor -> appointmentService.findAvailableAppointments(doctor.id(), searchDate).stream())
                    .toList();
            return availableTimes.isEmpty() ? "Свободных слотов не найдено." : availableTimes.toString();
        } catch (Exception e) {
            log.error("Error finding available appointments by specialization", e);
            return "Произошла ошибка при поиске доступных слотов: " + e.getMessage();
        }
    }

    @Tool(description = "Найти врача по ФИО")
    public String findDoctorByFullName(
            @ToolParam(description = """
                Полное имя врача.\s
                ВАЖНО: Сначала указывайте фамилию, затем имя, в конце отчество.
                Например: "Иванов Иван Иванович" или "Петрова Анна"
               \s""")
            String fullName
    ) {
        log.info("Find doctor by full name={}", fullName);
        try {
            final List<DoctorResponseDTO> doctors = doctorService.findByFullName(fullName);
            return doctors.isEmpty() ? "Врачи не найдены." : doctors.toString();
        } catch (Exception e) {
            log.error("Error finding doctor by full name", e);
            return "Произошла ошибка при поиске врача: " + e.getMessage();
        }
    }

    @Tool(description = "Найти врача по специальности")
    public String findDoctorBySpecialization(@ToolParam(description = "Специальность врача для поиска") String specialization) {
        log.info("Find doctor by specialization={}", specialization);
        try {
            final List<DoctorResponseDTO> doctors = doctorService.findBySpecialization(specialization);
            return doctors.isEmpty() ? "Врачи по данной специальности не найдены." : doctors.toString();
        } catch (Exception e) {
            log.error("Error finding doctor by specialization", e);
            return "Произошла ошибка при поиске врача: " + e.getMessage();
        }
    }

    @Tool(description = "Записать пациента на прием к врачу. " +
            "Используйте этот инструмент, когда пациент хочет записаться на конкретное время.")
    public String createAppointment(@ToolParam(description = "ID расписания врача. Можно получить через поиск расписания") Long scheduleId,
                                                    @ToolParam(description = "Детали записи: ID пациента, желаемые дата и время начала") AppointmentDTO appointmentDTO) {
        log.info("Create appointment: scheduleId={}, DTO={}", scheduleId, appointmentDTO);
        try {
            final AppointmentResponseDTO response = appointmentService.create(appointmentDTO, scheduleId);
            return "Запись успешно создана: " + response.toString();
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            return "Не удалось создать запись: " + e.getMessage();
        }
    }

    @Tool(description = "Отменить существующую запись на прием")
    public String cancelAppointment(@ToolParam(description = "ID записи") Long appointmentId) {
        log.info("Cancel appointment: ID={}", appointmentId);
        try {
            appointmentService.delete(appointmentId);
            return "Запись успешно отменена.";
        } catch (Exception e) {
            log.error("Error cancelling appointment", e);
            return "Произошла ошибка при отмене записи: " + e.getMessage();
        }
    }

    @Tool(description = "Получить историю болезни (медицинскую карту) пациента по его ID")
    public String getPatientMedicalHistory(@ToolParam(description = "ID пациента") Long patientId) {
        log.info("Get medical history for patientId={}", patientId);
        try {
            final Patient patient = patientService.getById(patientId);
            final MedicalCardResponseDTO medicalCard = medicalCardMapper.toMedicalCard(medicalCardService.getMedicalCardByPatient(patient));
            if (medicalCard == null || medicalCard.getMedicalRecord() == null || medicalCard.getMedicalRecord().isEmpty()) {
                return "Медицинская карта пуста или не найдена.";
            }
            return "История болезни пациента " + patient.getLastName() + " " + patient.getFirstName() + ": " + medicalCard.getMedicalRecord();
        } catch (Exception e) {
            log.error("Error getting patient medical history", e);
            return "Произошла ошибка при получении медицинской карты: " + e.getMessage();
        }
    }

    @Tool(description = "Получить расписание врача на конкретную дату")
    public String getDoctorSchedule(@ToolParam(description = "ID врача") Long doctorId,
                                                       @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        log.info("Get doctor with id={} schedule by date={}", doctorId, date);
        try {
            final LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            final Schedule doctorSchedule = scheduleService.getDoctorScheduleByDate(doctorId, scheduleDate);
            if (doctorSchedule == null) {
                return "Расписание не найдено на указанную дату.";
            }
            return scheduleMapper.toScheduleResponseDTO(doctorSchedule).toString();
        } catch (Exception e) {
            log.error("Error getting doctor schedule", e);
            return "Произошла ошибка при получении расписания: " + e.getMessage();
        }
    }

    @Tool(description = "Найти пациента по ФИО")
    public String findPatientByFullName(
            @ToolParam(description = """
                Полное имя пациента.\s
                ВАЖНО: Сначала указывайте фамилию, затем имя, в конце отчество.
                Например: "Иванов Иван Иванович" или "Петрова Анна"
               \s""")
            String fullName
    ) {
        log.info("Find patient by full name={}", fullName);
        try {
            final List<PatientResponseDTO> patients = patientService.findByFullName(fullName);
            return patients.isEmpty() ? "Пациенты не найдены." : patients.toString();
        } catch (Exception e) {
            log.error("Error finding patient by full name", e);
            return "Произошла ошибка при поиске пациента: " + e.getMessage();
        }
    }

    @Tool(description = "Получить текущую дату и время. Используй для определения 'сегодня', 'завтра', 'сейчас'")
    public String getCurrentDateTime() {
        try {
            final LocalDateTime now = LocalDateTime.now();
            log.info("Запрос текущего времени: {}", now);
            final CurrentDateTime currentDateTime = new CurrentDateTime(
                    now.format(DateTimeFormatter.ISO_DATE),
                    now.format(DateTimeFormatter.ofPattern("HH:mm")),
                    now.getDayOfWeek().toString(),
                    now
            );
            return currentDateTime.toString();
        } catch (Exception e) {
            log.error("Error getting current date time", e);
            return "Ошибка при получении текущего времени: " + e.getMessage();
        }
    }
}
