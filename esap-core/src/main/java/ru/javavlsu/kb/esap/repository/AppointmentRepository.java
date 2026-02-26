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

    List<Appointment> findBySchedule(Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.status = 'CONFIRMED'")
    List<Appointment> findByPatient(@Param("patient") Patient patient);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'CONFIRMED'")
    List<Appointment> findByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT NEW ru.javavlsu.kb.esap.dto.AppointmentsCountByDayDTO(a.date, COUNT(a)) " +
            "FROM Appointment a WHERE a.schedule.doctor.clinic = :clinic AND a.status = 'CONFIRMED' " +
            "GROUP BY a.date ORDER BY a.date ASC")
    List<AppointmentsCountByDayDTO> countAppointmentsByDay(@Param("clinic") Clinic clinic);

    @Query("SELECT a FROM Appointment a WHERE a.startAppointments = :startAppointment AND a.date = :date AND a.schedule = :schedule AND a.status = 'CONFIRMED'")
    List<Appointment> findByStartAppointmentsAndDateAndSchedule(LocalTime startAppointment, LocalDate date, Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE ((a.date = :currentDate " +
            "AND a.schedule.doctor = :doctor) OR (a.date < :currentDate AND a.schedule.doctor = :doctor)) AND a.status = 'CONFIRMED' " +
            "ORDER BY a.date DESC, a.startAppointments DESC")
    Page<Appointment> findLatestAppointments(@Param("doctor") Doctor doctor, @Param("currentDate") LocalDate currentDate, Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient " +
            "AND (a.date > :today OR (a.date = :today AND a.startAppointments >= :currentTime)) AND a.status = 'CONFIRMED' " +
            "ORDER BY a.date ASC, a.startAppointments ASC")
    Optional<Appointment> findUpcomingAppointmentByPatient(
            @Param("patient") Patient patient,
            @Param("today") LocalDate today,
            @Param("currentTime") LocalTime currentTime
    );

    @Query("SELECT a.startAppointments FROM Appointment a WHERE a.schedule.id = :scheduleId AND a.status = 'CONFIRMED'")
    List<LocalTime> findStartTimesByScheduleId(@Param("scheduleId") Long scheduleId);
}
