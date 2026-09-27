package ru.javavlsu.kb.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.javavlsu.kb.schedule.model.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByDoctorId(Long id);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date = :date")
    Optional<Schedule> findAllByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.clinicId = :clinicId AND s.date = :date")
    List<Schedule> findAllByClinic(@Param("date") LocalDate date, @Param("clinicId") Long clinicId);

    @Query("SELECT s FROM Schedule s JOIN FETCH s.appointments a WHERE s.doctor.id = :doctorId AND s.id = :id " +
            "ORDER BY a.startAppointments ASC")
    Optional<Schedule> findByIdAndDoctorIdOrderByAppointmentStartAppointmentsAsc(@Param("id") Long id, @Param("doctorId") Long doctorId);

    void deleteSchedulesByDateBefore(LocalDate date);

    List<Schedule> findSchedulesByDateBetween(LocalDate lastWeek, LocalDate today);

    boolean existsByDateAndDoctorId(LocalDate date, Long doctorId);
}
