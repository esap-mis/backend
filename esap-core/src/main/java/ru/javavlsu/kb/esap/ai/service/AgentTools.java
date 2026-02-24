package ru.javavlsu.kb.esap.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import ru.javavlsu.kb.esap.dto.AppointmentDTO;
import ru.javavlsu.kb.esap.dto.CurrentDateTime;
import ru.javavlsu.kb.esap.dto.DoctorResponseDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.AppointmentResponseDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.PatientResponseDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.ScheduleResponseDTO;
import ru.javavlsu.kb.esap.mapper.ScheduleMapper;
import ru.javavlsu.kb.esap.model.Schedule;
import ru.javavlsu.kb.esap.service.AppointmentService;
import ru.javavlsu.kb.esap.service.DoctorService;
import ru.javavlsu.kb.esap.service.PatientService;
import ru.javavlsu.kb.esap.service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AgentTools 17.02.2026 Alexey Karabanov
 * Copyright (c) 2026 WINGS.
 */
@Slf4j
@Service
public class AgentTools {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final ScheduleMapper scheduleMapper;

    public AgentTools(DoctorService doctorService, PatientService patientService, ScheduleService scheduleService, AppointmentService appointmentService, ScheduleMapper scheduleMapper) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
        this.scheduleMapper = scheduleMapper;
    }

    @Tool(description = "Найти доступные места для записи к врачу по специальности и дате")
    public List<LocalTime> findAvailableAppointmentsByFullName(@ToolParam(description = "Фамилия, имя или отчество врача") String fullName,
                                                     @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        log.info("Find available times for new appointment: fullName={}, date={}", fullName, date);
        final LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        final List<DoctorResponseDTO> doctors = doctorService.findByFullName(fullName);
        return doctors.stream()
                .flatMap(doctor -> appointmentService.findAvailableAppointments(doctor.id(), searchDate).stream())
                .toList();
    }

    @Tool(description = "Найти доступные места для записи к врачу по специальности и дате")
    public List<LocalTime> findAvailableAppointmentsBySpec(@ToolParam(description = "Специальность врача (например: терапевт, хирург, окулист)") String specialization,
                                                     @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        log.info("Find available times for new appointment: specialization={}, date={}", specialization, date);
        final LocalDate searchDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        final List<DoctorResponseDTO> doctors = doctorService.findBySpecialization(specialization);
        return doctors.stream()
                .flatMap(doctor -> appointmentService.findAvailableAppointments(doctor.id(), searchDate).stream())
                .toList();
    }

    @Tool(description = "Найти врача по ФИО")
    public List<DoctorResponseDTO> findDoctorByFullName(@ToolParam(description = "Фамилия, имя или отчество врача") String fullName) {
        log.info("Find doctor by full name={}", fullName);
        return doctorService.findByFullName(fullName);
    }

    @Tool(description = "Найти врача по специальности")
    public List<DoctorResponseDTO> findDoctorBySpecialization(@ToolParam(description = "Специальность врача для поиска") String specialization) {
        log.info("Find doctor by specialization={}", specialization);
        return doctorService.findBySpecialization(specialization);
    }

    @Tool(description = "Записать пациента на прием к врачу. " +
            "Используйте этот инструмент, когда пациент хочет записаться на конкретное время.")
    public AppointmentResponseDTO createAppointment(@ToolParam(description = "ID расписания врача. Можно получить через поиск расписания") Long scheduleId,
                                                    @ToolParam(description = "Детали записи: ID пациента, желаемые дата и время начала") AppointmentDTO appointmentDTO) {
        log.info("Create appointment: scheduleId={}, DTO={}", scheduleId, appointmentDTO);
        return appointmentService.create(appointmentDTO, scheduleId);
    }

//    @Tool(description = "Отменить существующую запись на прием")
//    public AppointmentResult cancelAppointment(@ToolParam(description = "ID записи") Long appointmentId,
//                                               @ToolParam(description = "Причина отмены") String reason) {
//        log.info("Отмена записи: ID={}, причина={}", appointmentId, reason);
//        boolean cancelled = appointmentService.cancelAppointment(appointmentId, reason);
//        if (cancelled) {
//            return new AppointmentResult(true, "Запись успешно отменена", null);
//        } else {
//            return new AppointmentResult(false, "Не удалось отменить запись", null);
//        }
//    }

    @Tool(description = "Получить расписание врача на конкретную дату")
    public ScheduleResponseDTO getDoctorSchedule(@ToolParam(description = "ID врача") Long doctorId,
                                                       @ToolParam(description = "Дата в формате ГГГГ-ММ-ДД") String date) {
        log.info("Get doctor with id={} schedule by date={}", doctorId, date);
        final LocalDate scheduleDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        final Schedule doctorSchedule = scheduleService.getDoctorScheduleByDate(doctorId, scheduleDate);
        return scheduleMapper.toScheduleResponseDTO(doctorSchedule);
    }

    @Tool(description = "Найти пациента по ФИО")
    public List<PatientResponseDTO> findPatientByFullName(@ToolParam(description = "Фамилия, имя или отчество пациента") String fullName) {
        log.info("Find patient by full name={}", fullName);
        return patientService.findByFullName(fullName);
    }

    @Tool(description = "Получить текущую дату и время. Используй для определения 'сегодня', 'завтра', 'сейчас'")
    public CurrentDateTime getCurrentDateTime() {
        final LocalDateTime now = LocalDateTime.now();
        log.info("Запрос текущего времени: {}", now);
        return new CurrentDateTime(
                now.format(DateTimeFormatter.ISO_DATE),
                now.format(DateTimeFormatter.ofPattern("HH:mm")),
                now.getDayOfWeek().toString(),
                now
        );
    }
}
