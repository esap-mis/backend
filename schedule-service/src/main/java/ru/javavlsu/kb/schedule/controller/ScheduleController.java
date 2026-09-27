package ru.javavlsu.kb.schedule.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.common.web.NotCreateException;
import ru.javavlsu.kb.common.web.NotFoundException;
import ru.javavlsu.kb.common.web.ResponseMessageError;
import ru.javavlsu.kb.schedule.dto.*;
import ru.javavlsu.kb.schedule.mapper.ScheduleMapper;
import ru.javavlsu.kb.schedule.service.AppointmentService;
import ru.javavlsu.kb.schedule.service.DoctorRefService;
import ru.javavlsu.kb.schedule.service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final AppointmentService appointmentService;
    private final DoctorRefService doctorRefService;
    private final ScheduleMapper scheduleMapper;
    private final CurrentUser currentUser;

    public ScheduleController(ScheduleService scheduleService, AppointmentService appointmentService,
                              DoctorRefService doctorRefService, ScheduleMapper scheduleMapper, CurrentUser currentUser) {
        this.scheduleService = scheduleService;
        this.appointmentService = appointmentService;
        this.doctorRefService = doctorRefService;
        this.scheduleMapper = scheduleMapper;
        this.currentUser = currentUser;
    }

    @GetMapping("/doctor/{doctorId}")
    public List<ScheduleResponseDTO> getDoctorSchedule(@PathVariable("doctorId") Long doctorId) {
        return scheduleService.getAllByDoctorId(doctorId).stream().map(scheduleMapper::toScheduleResponseDTO).toList();
    }

    @GetMapping("/{id}")
    public ScheduleResponseDTO getSchedule(@PathVariable("id") Long id) {
        return scheduleMapper.toScheduleResponseDTO(scheduleService.getByIdAndDoctor(id, currentUser.id()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> createSchedule(@RequestBody @Valid ScheduleDTO scheduleDTO,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        return ResponseEntity.ok(Map.of("scheduleId", scheduleService.create(scheduleDTO).getId()));
    }

    @PostMapping("/{id}/appointment")
    public ResponseEntity<AppointmentResponseDTO> addAppointment(@PathVariable("id") Long id,
                                                                 @RequestBody @Valid AppointmentDTO appointmentDTO,
                                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        return ResponseEntity.ok(appointmentService.create(appointmentDTO, id));
    }

    @GetMapping("/day")
    public List<ScheduleResponseDTO> getAppointmentsByDay(@RequestParam(required = false) LocalDate date) {
        return scheduleService.getSchedulesByDay(date, requireClinicId());
    }

    @GetMapping("/appointment/latest")
    public List<AppointmentResponseDTO> getLatestAppointments(@RequestParam(name = "count", defaultValue = "5") Integer count) {
        return appointmentService.getLatestAppointments(count, currentUser.id());
    }

    @GetMapping("/appointment/count-by-day")
    @PreAuthorize("hasAnyRole('CHIEF_DOCTOR', 'LABORATORY', 'REGISTRANT', 'DOCTOR', 'ADMIN')")
    public List<AppointmentsCountByDayDTO> getAppointmentsCountByDay() {
        return appointmentService.getAppointmentsCountByDay(requireClinicId());
    }

    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<?>> getUpcomingUserAppointments() {
        return ResponseEntity.ok(currentUser.isPatient()
                ? appointmentService.getUpcomingAppointmentsForPatient(currentUser.id())
                : appointmentService.getUpcomingAppointmentsForDoctor(currentUser.id()));
    }

    @GetMapping("/appointments/past")
    public ResponseEntity<List<?>> getPastUserAppointments() {
        return ResponseEntity.ok(currentUser.isPatient()
                ? appointmentService.getPastAppointmentsForPatient(currentUser.id())
                : appointmentService.getPastAppointmentsForDoctor(currentUser.id()));
    }

    @GetMapping("/available")
    public List<LocalTime> getAvailableAppointments(@RequestParam Long doctorId, @RequestParam LocalDate date) {
        return appointmentService.getAvailableAppointmentsForDoctor(doctorId, date);
    }

    @GetMapping("/doctors")
    public List<DoctorRefDTO> getDoctors(@RequestParam(required = false) String fullName,
                                         @RequestParam(required = false) String specialization) {
        if (fullName != null && !fullName.isBlank()) {
            return doctorRefService.findByFullName(fullName);
        }
        if (specialization != null && !specialization.isBlank()) {
            return doctorRefService.findBySpecialization(specialization);
        }
        return doctorRefService.findByClinic(requireClinicId());
    }

    @DeleteMapping("/appointment/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable("id") Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok().build();
    }

    private Long requireClinicId() {
        Long clinicId = currentUser.clinicId();
        if (clinicId == null) {
            throw new NotFoundException("Clinic not found for current user");
        }
        return clinicId;
    }
}
