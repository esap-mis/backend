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

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findBySchedule(Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE a.patient = :patient")
    List<Appointment> findByPatient(@Param("patient") Patient patient);

    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor")
    List<Appointment> findByDoctor(@Param("doctor") Doctor doctor);

    @Query("SELECT NEW ru.javavlsu.kb.esap.dto.AppointmentsCountByDayDTO(a.date, COUNT(a)) " +
            "FROM Appointment a WHERE a.schedule.doctor.clinic = :clinic " +
            "GROUP BY a.date ORDER BY a.date ASC")
    List<AppointmentsCountByDayDTO> countAppointmentsByDay(@Param("clinic") Clinic clinic);

    List<Appointment> findByStartAppointmentsAndDateAndSchedule(LocalTime startAppointment, LocalDate date, Schedule schedule);

    @Query("SELECT a FROM Appointment a WHERE (a.date = :currentDate AND a.schedule.doctor = :doctor) OR (a.date < :currentDate AND a.schedule.doctor = :doctor) ORDER BY a.date DESC, a.startAppointments DESC")
    Page<Appointment> findLatestAppointments(@Param("doctor") Doctor doctor, @Param("currentDate") LocalDate currentDate, Pageable pageable);
}
