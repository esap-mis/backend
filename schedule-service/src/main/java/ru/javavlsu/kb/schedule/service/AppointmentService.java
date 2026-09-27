package ru.javavlsu.kb.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.common.event.AppointmentCreatedEvent;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.web.NotCreateException;
import ru.javavlsu.kb.common.web.NotFoundException;
import ru.javavlsu.kb.schedule.dto.AppointmentDTO;
import ru.javavlsu.kb.schedule.dto.AppointmentResponseDTO;
import ru.javavlsu.kb.schedule.dto.AppointmentsCountByDayDTO;
import ru.javavlsu.kb.schedule.dto.DoctorAppointmentDTO;
import ru.javavlsu.kb.schedule.dto.PatientAppointmentDTO;
import ru.javavlsu.kb.schedule.mapper.AppointmentMapper;
import ru.javavlsu.kb.schedule.mapper.AppointmentViewMapper;
import ru.javavlsu.kb.schedule.model.Appointment;
import ru.javavlsu.kb.schedule.model.AppointmentStatus;
import ru.javavlsu.kb.schedule.model.DoctorRef;
import ru.javavlsu.kb.schedule.model.PatientRef;
import ru.javavlsu.kb.schedule.model.Schedule;
import ru.javavlsu.kb.schedule.repository.AppointmentRepository;
import ru.javavlsu.kb.schedule.repository.DoctorRefRepository;
import ru.javavlsu.kb.schedule.repository.PatientRefRepository;
import ru.javavlsu.kb.schedule.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentService {

    private static final int SLOT_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRefRepository patientRefRepository;
    private final DoctorRefRepository doctorRefRepository;
    private final AppointmentMapper appointmentMapper;
    private final AppointmentViewMapper appointmentViewMapper;
    private final ScheduleService scheduleService;
    private final EventPublisher eventPublisher;

    @Transactional
    public AppointmentResponseDTO create(AppointmentDTO dto, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        List<Appointment> scheduleAppointments = appointmentRepository.findBySchedule(schedule);
        if (schedule.getMaxPatientPerDay() == scheduleAppointments.size()) {
            throw new NotCreateException("No free time found in the schedule");
        }
        boolean taken = !appointmentRepository
                .findByStartAppointmentsAndDateAndSchedule(dto.startAppointments(), dto.date(), schedule).isEmpty();
        if (taken) {
            throw new NotCreateException("Time is already taken");
        }

        PatientRef patient = patientRefRepository.findById(dto.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        DoctorRef doctor = doctorRefRepository.findById(schedule.getDoctor().getId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setSchedule(schedule);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDate(dto.date());
        appointment.setStartAppointments(dto.startAppointments());
        appointment.setEndAppointments(dto.startAppointments().plusMinutes(SLOT_MINUTES));
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        Appointment saved = appointmentRepository.save(appointment);
        eventPublisher.sendAppointmentCreatedEvent(new AppointmentCreatedEvent(
                saved.getId(), scheduleId, patient.getId(), doctor.getId(),
                saved.getDate(), saved.getStartAppointments(), saved.getEndAppointments(), saved.getStatus().name()));
        return appointmentMapper.toAppointmentResponseDTO(saved);
    }

    public List<AppointmentResponseDTO> getLatestAppointments(Integer count, Long doctorId) {
        DoctorRef doctor = requireDoctor(doctorId);
        List<Appointment> appointments = appointmentRepository
                .findLatestAppointments(doctor, LocalDate.now(), PageRequest.of(0, count)).getContent();
        return appointmentMapper.toAppointmentResponseDTOList(appointments);
    }

    public List<AppointmentsCountByDayDTO> getAppointmentsCountByDay(Long clinicId) {
        return appointmentRepository.countAppointmentsByDay(clinicId);
    }

    public List<DoctorAppointmentDTO> getAppointmentsForDoctor(Long doctorId) {
        return appointmentViewMapper.toDoctorAppointmentDTOList(
                appointmentRepository.findByDoctor(requireDoctor(doctorId)));
    }

    public List<PatientAppointmentDTO> getAppointmentsForPatient(Long patientId) {
        return appointmentViewMapper.toPatientAppointmentDTOList(
                appointmentRepository.findByPatient(requirePatient(patientId)));
    }

    public Optional<Appointment> getUpcomingAppointmentByPatient(Long patientId) {
        return appointmentRepository.findUpcomingByPatient(requirePatient(patientId), LocalDate.now(), LocalTime.now())
                .stream().findFirst();
    }

    public List<PatientAppointmentDTO> getUpcomingAppointmentsForPatient(Long patientId) {
        return appointmentViewMapper.toPatientAppointmentDTOList(
                appointmentRepository.findUpcomingByPatient(requirePatient(patientId), LocalDate.now(), LocalTime.now()));
    }

    public List<DoctorAppointmentDTO> getUpcomingAppointmentsForDoctor(Long doctorId) {
        return appointmentViewMapper.toDoctorAppointmentDTOList(
                appointmentRepository.findUpcomingByDoctor(requireDoctor(doctorId), LocalDate.now(), LocalTime.now()));
    }

    public List<PatientAppointmentDTO> getPastAppointmentsForPatient(Long patientId) {
        return appointmentViewMapper.toPatientAppointmentDTOList(
                appointmentRepository.findPastByPatient(requirePatient(patientId), LocalDate.now(), LocalTime.now()));
    }

    public List<DoctorAppointmentDTO> getPastAppointmentsForDoctor(Long doctorId) {
        return appointmentViewMapper.toDoctorAppointmentDTOList(
                appointmentRepository.findPastByDoctor(requireDoctor(doctorId), LocalDate.now(), LocalTime.now()));
    }

    public List<LocalTime> findAvailableAppointments(Long doctorId, LocalDate date) {
        Schedule schedule = scheduleService.getDoctorScheduleByDate(doctorId, date);
        List<LocalTime> booked = appointmentRepository.findStartTimesByScheduleId(schedule.getId());
        List<LocalTime> allSlots = generateTimeSlots(schedule.getStartDoctorAppointment(), schedule.getEndDoctorAppointment());
        return allSlots.stream()
                .filter(slot -> !booked.contains(slot))
                .limit(schedule.getMaxPatientPerDay() - booked.size())
                .toList();
    }

    public List<LocalTime> getAvailableAppointmentsForDoctor(Long doctorId, LocalDate date) {
        return findAvailableAppointments(doctorId, date);
    }

    @Transactional
    public void cancelAppointment(long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment with id=" + id + " not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private List<LocalTime> generateTimeSlots(LocalTime start, LocalTime end) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime current = start;
        while (current.isBefore(end)) {
            slots.add(current);
            current = current.plusMinutes(SLOT_MINUTES);
        }
        return slots;
    }

    private DoctorRef requireDoctor(Long doctorId) {
        return doctorRefRepository.findById(doctorId).orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    private PatientRef requirePatient(Long patientId) {
        return patientRefRepository.findById(patientId).orElseThrow(() -> new NotFoundException("Patient not found"));
    }
}
