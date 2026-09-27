package ru.javavlsu.kb.schedule.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.schedule.dto.AppointmentsCountByDayDTO;
import ru.javavlsu.kb.schedule.model.Appointment;
import ru.javavlsu.kb.schedule.model.DoctorRef;
import ru.javavlsu.kb.schedule.model.PatientRef;
import ru.javavlsu.kb.schedule.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findBySchedule(Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.status = 'CONFIRMED'")
    List<Appointment> findByPatient(@Param("patient") PatientRef patient);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'CONFIRMED'")
    List<Appointment> findByDoctor(@Param("doctor") DoctorRef doctor);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.status = 'CONFIRMED' " +
            "AND (a.date > :today OR (a.date = :today AND a.startAppointments >= :currentTime)) " +
            "ORDER BY a.date ASC, a.startAppointments ASC")
    List<Appointment> findUpcomingByPatient(@Param("patient") PatientRef patient, @Param("today") LocalDate today,
                                            @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient AND a.status = 'CONFIRMED' " +
            "AND (a.date < :today OR (a.date = :today AND a.startAppointments < :currentTime)) " +
            "ORDER BY a.date DESC, a.startAppointments DESC")
    List<Appointment> findPastByPatient(@Param("patient") PatientRef patient, @Param("today") LocalDate today,
                                        @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'CONFIRMED' " +
            "AND (a.date > :today OR (a.date = :today AND a.startAppointments >= :currentTime)) " +
            "ORDER BY a.date ASC, a.startAppointments ASC")
    List<Appointment> findUpcomingByDoctor(@Param("doctor") DoctorRef doctor, @Param("today") LocalDate today,
                                           @Param("currentTime") LocalTime currentTime);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'CONFIRMED' " +
            "AND (a.date < :today OR (a.date = :today AND a.startAppointments < :currentTime)) " +
            "ORDER BY a.date DESC, a.startAppointments DESC")
    List<Appointment> findPastByDoctor(@Param("doctor") DoctorRef doctor, @Param("today") LocalDate today,
                                       @Param("currentTime") LocalTime currentTime);

    @Query("SELECT NEW ru.javavlsu.kb.schedule.dto.AppointmentsCountByDayDTO(a.date, COUNT(a)) " +
            "FROM Appointment a WHERE a.schedule.doctor.clinicId = :clinicId AND a.status = 'CONFIRMED' " +
            "GROUP BY a.date ORDER BY a.date ASC")
    List<AppointmentsCountByDayDTO> countAppointmentsByDay(@Param("clinicId") Long clinicId);

    @Query("SELECT a FROM Appointment a WHERE a.startAppointments = :startAppointment AND a.date = :date " +
            "AND a.schedule = :schedule AND a.status = 'CONFIRMED'")
    List<Appointment> findByStartAppointmentsAndDateAndSchedule(LocalTime startAppointment, LocalDate date,
                                                               Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE ((a.date = :currentDate AND a.doctor = :doctor) " +
            "OR (a.date < :currentDate AND a.doctor = :doctor)) AND a.status = 'CONFIRMED' " +
            "ORDER BY a.date DESC, a.startAppointments DESC")
    Page<Appointment> findLatestAppointments(@Param("doctor") DoctorRef doctor, @Param("currentDate") LocalDate currentDate,
                                             Pageable pageable);

    @Query("SELECT a.startAppointments FROM Appointment a WHERE a.schedule.id = :scheduleId AND a.status = 'CONFIRMED'")
    List<LocalTime> findStartTimesByScheduleId(@Param("scheduleId") Long scheduleId);
}
