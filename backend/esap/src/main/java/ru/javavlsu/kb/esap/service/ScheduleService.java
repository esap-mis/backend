package ru.javavlsu.kb.esap.service;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.dto.ScheduleDTO;
import ru.javavlsu.kb.esap.mapper.ScheduleMapper;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.Schedule;
import ru.javavlsu.kb.esap.repository.DoctorRepository;
import ru.javavlsu.kb.esap.repository.ScheduleRepository;
import ru.javavlsu.kb.esap.exception.NotCreateException;
import ru.javavlsu.kb.esap.exception.NotFoundException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final DoctorRepository doctorRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper, DoctorRepository doctorRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public void create(ScheduleDTO scheduleDTO) throws NotCreateException {
        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        Doctor doctor = doctorRepository.findById(scheduleDTO.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));
        schedule.setDoctor(doctor);
        long minutesBetweenStartAndEnd = schedule.getStartDoctorAppointment().until(schedule.getEndDoctorAppointment(), ChronoUnit.MINUTES);
        if (minutesBetweenStartAndEnd <= 0 && minutesBetweenStartAndEnd % 30 != 0) {
            throw new NotCreateException("Invalid schedule time");
        }
        schedule.setMaxPatientPerDay(((int) minutesBetweenStartAndEnd / 30) + 1);
        scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllByDoctorId(Long doctorId) {
        return scheduleRepository.findAllByDoctorId(doctorId);
    }

    public Schedule getByIdAndDoctor(long id, Doctor doctor) {
        Optional<Schedule> schedule = scheduleRepository.findByIdAndDoctorOrderByAppointmentStartAppointmentsAsc(id, doctor);
        return schedule.orElseThrow(() -> new NotFoundException("Schedule not found"));
    }

    public Schedule getByDateAndDoctor(LocalDate date, Doctor doctor) throws NotCreateException {
        return scheduleRepository.findByDateAndDoctor(date, doctor)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
    }

    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 7)
    public void deleteOverdueSchedules() {
        scheduleRepository.deleteScheduleByDateBefore(LocalDate.now());
    }
}
