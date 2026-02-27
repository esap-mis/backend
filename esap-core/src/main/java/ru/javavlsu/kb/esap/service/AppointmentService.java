package ru.javavlsu.kb.esap.service;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.dto.AppointmentsCountByDayDTO;
import ru.javavlsu.kb.esap.dto.AppointmentDTO;
import ru.javavlsu.kb.esap.dto.DoctorAppointmentDTO;
import ru.javavlsu.kb.esap.dto.PatientAppointmentDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.AppointmentResponseDTO;
import ru.javavlsu.kb.esap.mapper.AppointmentMapper;
import ru.javavlsu.kb.esap.model.*;
import ru.javavlsu.kb.esap.repository.AppointmentRepository;
import ru.javavlsu.kb.esap.repository.PatientRepository;
import ru.javavlsu.kb.esap.repository.ScheduleRepository;
import ru.javavlsu.kb.esap.repository.TimeSlotRepository;
import ru.javavlsu.kb.esap.exception.NotCreateException;
import ru.javavlsu.kb.esap.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorService doctorService;
    private final EntityManager em;
    private final ScheduleService scheduleService;
    private final TimeSlotRepository timeSlotRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository, PatientRepository patientRepository, AppointmentMapper appointmentMapper, DoctorService doctorService, EntityManager em, ScheduleService scheduleService, TimeSlotRepository timeSlotRepository) {
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.patientRepository = patientRepository;
        this.appointmentMapper = appointmentMapper;
        this.doctorService = doctorService;
        this.em = em;
        this.scheduleService = scheduleService;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentDTO appointmentDTO, long scheduleId) throws NotCreateException {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        TimeSlot timeSlot = schedule.getTimeSlots().stream()
                .filter(slot -> slot.getStartTime().equals(appointmentDTO.startAppointments()) && slot.getIsAvailable())
                .findFirst()
                .orElseThrow(() -> new NotCreateException("Time is already taken or not found"));

        Appointment appointment = appointmentMapper.toAppointment(appointmentDTO);
        appointment.setSchedule(schedule);
        appointment.setTimeSlot(timeSlot);
        timeSlot.setIsAvailable(false);

        Patient patient = patientRepository.findById(appointmentDTO.patientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        appointment.setPatient(patient);
        appointment.setDoctor(doctorService.refreshDoctor(schedule.getDoctor()));
        final Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toAppointmentResponseDTO(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> getLatestAppointments(Integer count, Doctor doctor) {
        Pageable pageable = PageRequest.of(0, count);
        List<Appointment> appointments = appointmentRepository.findLatestAppointments(doctor, LocalDate.now(), pageable).stream().toList();
        return appointmentMapper.toAppointmentResponseDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public List<AppointmentsCountByDayDTO> getAppointmentsCountByDay(Doctor doctor) {
        return appointmentRepository.countAppointmentsByDay(doctor.getClinic());
    }

    @Transactional(readOnly = true)
    public List<DoctorAppointmentDTO> getAppointmentsForUser(Doctor doctor) {
        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);
        return appointmentMapper.toDoctorAppointmentDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public List<PatientAppointmentDTO> getAppointmentsForUser(Patient patient) {
        List<Appointment> appointments = appointmentRepository.findByPatient(patient);
        return appointmentMapper.toPatientAppointmentDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> getUpcomingAppointmentByPatient(Patient patient) {
        return appointmentRepository.findUpcomingByPatient(patient, LocalDate.now(), LocalTime.now())
                .stream()
                .findFirst();
    }

    @Transactional(readOnly = true)
    public List<PatientAppointmentDTO> getUpcomingAppointmentsForUser(Patient patient) {
        List<Appointment> appointments = appointmentRepository.findUpcomingByPatient(patient, LocalDate.now(), LocalTime.now());
        return appointmentMapper.toPatientAppointmentDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public List<DoctorAppointmentDTO> getUpcomingAppointmentsForUser(Doctor doctor) {
        List<Appointment> appointments = appointmentRepository.findUpcomingByDoctor(doctor, LocalDate.now(), LocalTime.now());
        return appointmentMapper.toDoctorAppointmentDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public List<PatientAppointmentDTO> getPastAppointmentsForUser(Patient patient) {
        List<Appointment> appointments = appointmentRepository.findPastByPatient(patient, LocalDate.now(), LocalTime.now());
        return appointmentMapper.toPatientAppointmentDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public List<DoctorAppointmentDTO> getPastAppointmentsForUser(Doctor doctor) {
        List<Appointment> appointments = appointmentRepository.findPastByDoctor(doctor, LocalDate.now(), LocalTime.now());
        return appointmentMapper.toDoctorAppointmentDTOList(appointments);
    }

    @Transactional(readOnly = true)
    public List<LocalTime> findAvailableAppointments(Long doctorId, LocalDate date) {
        final Schedule schedule = scheduleService.getDoctorScheduleByDate(doctorId, date);
        return schedule.getTimeSlots().stream()
                .filter(TimeSlot::getIsAvailable)
                .map(TimeSlot::getStartTime)
                .toList();
    }

    @Transactional
    public void cancelAppointment(long id) {
        final Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment with id=" + id + " not found"));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        if (appointment.getTimeSlot() != null) {
            appointment.getTimeSlot().setIsAvailable(true);
        }
        appointmentRepository.save(appointment);
    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void updatePastAppointmentsStatus() {
        List<Appointment> pastConfirmed = appointmentRepository.findPastConfirmedAppointments(LocalDate.now(), LocalTime.now());
        pastConfirmed.forEach(a -> a.setStatus(AppointmentStatus.COMPLETED));
        appointmentRepository.saveAll(pastConfirmed);
    }
}
