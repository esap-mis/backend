package ru.javavlsu.kb.esap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.esap.dto.AppointmentsCountByDayDTO;
import ru.javavlsu.kb.esap.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.schedule = :schedule AND a.status IN ('CONFIRMED', 'COMPLETED')")
    List<Appointment> findBySchedule(@Param("schedule") Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.status IN ('CONFIRMED', 'COMPLETED')")
    List<Appointment> findByPatient(@Param("patient") Patient patient);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status IN ('CONFIRMED', 'COMPLETED')")
    List<Appointment> findByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.status = 'CONFIRMED' " +
            "AND (a.schedule.date > :today OR (a.schedule.date = :today AND a.timeSlot.startTime >= :currentTime)) " +
            "ORDER BY a.schedule.date ASC, a.timeSlot.startTime ASC")
    List<Appointment> findUpcomingByPatient(@Param("patient") Patient patient, @Param("today") LocalDate today, @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND (a.status IN ('COMPLETED', 'CANCELLED') " +
            "OR (a.status = 'CONFIRMED' AND (a.schedule.date < :today OR (a.schedule.date = :today AND a.timeSlot.startTime < :currentTime)))) " +
            "ORDER BY a.schedule.date DESC, a.timeSlot.startTime DESC")
    List<Appointment> findPastByPatient(@Param("patient") Patient patient, @Param("today") LocalDate today, @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'CONFIRMED' " +
            "AND (a.schedule.date > :today OR (a.schedule.date = :today AND a.timeSlot.startTime >= :currentTime)) " +
            "ORDER BY a.schedule.date ASC, a.timeSlot.startTime ASC")
    List<Appointment> findUpcomingByDoctor(@Param("doctor") Doctor doctor, @Param("today") LocalDate today, @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
            "AND (a.status IN ('COMPLETED', 'CANCELLED') " +
            "OR (a.status = 'CONFIRMED' AND (a.schedule.date < :today OR (a.schedule.date = :today AND a.timeSlot.startTime < :currentTime)))) " +
            "ORDER BY a.schedule.date DESC, a.timeSlot.startTime DESC")
    List<Appointment> findPastByDoctor(@Param("doctor") Doctor doctor, @Param("today") LocalDate today, @Param("currentTime") LocalTime currentTime);

    @Query("SELECT NEW ru.javavlsu.kb.esap.dto.AppointmentsCountByDayDTO(a.schedule.date, COUNT(a)) " +
            "FROM Appointment a WHERE a.schedule.doctor.clinic = :clinic AND a.status IN ('CONFIRMED', 'COMPLETED') " +
            "GROUP BY a.schedule.date ORDER BY a.schedule.date ASC")
    List<AppointmentsCountByDayDTO> countAppointmentsByDay(@Param("clinic") Clinic clinic);

    @Query("SELECT a FROM Appointment a WHERE a.timeSlot.startTime = :startAppointment AND a.schedule.date = :date AND a.schedule = :schedule AND a.status IN ('CONFIRMED', 'COMPLETED')")
    List<Appointment> findByTimeSlotStartTimeAndDateAndSchedule(LocalTime startAppointment, LocalDate date, Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE ((a.schedule.date = :currentDate " +
            "AND a.schedule.doctor = :doctor) OR (a.schedule.date < :currentDate AND a.schedule.doctor = :doctor)) AND a.status IN ('CONFIRMED', 'COMPLETED') " +
            "ORDER BY a.schedule.date DESC, a.timeSlot.startTime DESC")
    Page<Appointment> findLatestAppointments(@Param("doctor") Doctor doctor, @Param("currentDate") LocalDate currentDate, Pageable pageable);

    @Query("SELECT a.timeSlot.startTime FROM Appointment a WHERE a.schedule.id = :scheduleId AND a.status IN ('CONFIRMED', 'COMPLETED')")
    List<LocalTime> findStartTimesByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT a FROM Appointment a WHERE a.status = 'CONFIRMED' AND (a.schedule.date < :today OR (a.schedule.date = :today AND a.timeSlot.endTime < :currentTime))")
    List<Appointment> findPastConfirmedAppointments(@Param("today") LocalDate today, @Param("currentTime") LocalTime currentTime);
}
