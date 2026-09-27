package ru.javavlsu.kb.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.common.web.NotCreateException;
import ru.javavlsu.kb.common.web.NotFoundException;
import ru.javavlsu.kb.schedule.dto.ScheduleDTO;
import ru.javavlsu.kb.schedule.dto.ScheduleResponseDTO;
import ru.javavlsu.kb.schedule.mapper.ScheduleMapper;
import ru.javavlsu.kb.schedule.model.DoctorRef;
import ru.javavlsu.kb.schedule.model.Schedule;
import ru.javavlsu.kb.schedule.repository.DoctorRefRepository;
import ru.javavlsu.kb.schedule.repository.ScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final DoctorRefRepository doctorRefRepository;

    @Transactional
    public Schedule create(ScheduleDTO scheduleDTO) {
        DoctorRef doctor = doctorRefRepository.findById(scheduleDTO.doctorId())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));
        scheduleExistsForDateAndDoctor(scheduleDTO.date(), doctor.getId());
        Schedule schedule = scheduleMapper.toSchedule(scheduleDTO);
        schedule.setDoctor(doctor);
        long minutes = schedule.getStartDoctorAppointment().until(schedule.getEndDoctorAppointment(), ChronoUnit.MINUTES);
        if (minutes <= 0 || minutes % 30 != 0) {
            throw new NotCreateException("Invalid schedule time");
        }
        schedule.setMaxPatientPerDay(((int) minutes / 30) + 1);
        return scheduleRepository.save(schedule);
    }

    public List<Schedule> getAllByDoctorId(Long doctorId) {
        return scheduleRepository.findAllByDoctorId(doctorId);
    }

    public Schedule getByIdAndDoctor(long id, Long doctorId) {
        return scheduleRepository.findByIdAndDoctorIdOrderByAppointmentStartAppointmentsAsc(id, doctorId)
                .orElseThrow(() -> new NotFoundException("Schedule not found"));
    }

    public List<ScheduleResponseDTO> getSchedulesByDay(LocalDate date, Long clinicId) {
        return scheduleMapper.toScheduleResponseDTOList(
                scheduleRepository.findAllByClinic(date != null ? date : LocalDate.now(), clinicId));
    }

    public Schedule getDoctorScheduleByDate(Long doctorId, LocalDate date) {
        return scheduleRepository.findAllByDoctorAndDate(doctorId, date)
                .orElseThrow(() -> new NotFoundException("Schedule not found for doctor with id=" + doctorId + " on date=" + date));
    }

    /**
     * Копирует расписание прошлой недели на текущую; при первом запуске
     * создаёт дефолтное расписание на неделю вперёд.
     */
    @Transactional
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 7)
    public void copyAndDeleteSchedules() {
        LocalDate today = LocalDate.now();
        LocalDate startOfCurrentWeek = today.with(DayOfWeek.MONDAY);
        LocalDate startOfLastWeek = startOfCurrentWeek.minusWeeks(1);

        List<Schedule> lastWeekSchedules = scheduleRepository.findSchedulesByDateBetween(startOfLastWeek, startOfCurrentWeek);

        if (!lastWeekSchedules.isEmpty()) {
            List<Schedule> newSchedules = lastWeekSchedules.stream()
                    .filter(schedule -> !scheduleRepository.existsByDateAndDoctorId(
                            today.with(DayOfWeek.of(schedule.getDate().getDayOfWeek().getValue())), schedule.getDoctor().getId()))
                    .map(this::copyScheduleForToday)
                    .collect(Collectors.toList());
            scheduleRepository.saveAll(newSchedules);
        } else {
            for (int i = 0; i < 5; i++) {
                createDefaultSchedules(startOfCurrentWeek.plusDays(i));
            }
        }
        scheduleRepository.deleteSchedulesByDateBefore(startOfCurrentWeek.minusDays(1));
    }

    private Schedule copyScheduleForToday(Schedule lastWeekSchedule) {
        Schedule newSchedule = new Schedule();
        newSchedule.setDate(lastWeekSchedule.getDate().plusWeeks(1));
        newSchedule.setDoctor(lastWeekSchedule.getDoctor());
        newSchedule.setMaxPatientPerDay(lastWeekSchedule.getMaxPatientPerDay());
        newSchedule.setStartDoctorAppointment(lastWeekSchedule.getStartDoctorAppointment());
        newSchedule.setEndDoctorAppointment(lastWeekSchedule.getEndDoctorAppointment());
        return newSchedule;
    }

    private void createDefaultSchedules(LocalDate date) {
        doctorRefRepository.findAll().forEach(doctor -> {
            if (!scheduleRepository.existsByDateAndDoctorId(date, doctor.getId())) {
                Schedule defaultSchedule = new Schedule();
                defaultSchedule.setDoctor(doctor);
                defaultSchedule.setDate(date);
                LocalTime start = LocalTime.of(8, 0);
                LocalTime end = LocalTime.of(17, 0);
                defaultSchedule.setStartDoctorAppointment(start);
                defaultSchedule.setEndDoctorAppointment(end);
                long minutes = start.until(end, ChronoUnit.MINUTES);
                defaultSchedule.setMaxPatientPerDay(((int) minutes / 30) + 1);
                scheduleRepository.save(defaultSchedule);
            }
        });
    }

    private void scheduleExistsForDateAndDoctor(LocalDate date, Long doctorId) {
        if (scheduleRepository.existsByDateAndDoctorId(date, doctorId)) {
            throw new NotCreateException("Schedule already exists for the specified date and doctor");
        }
    }
}
