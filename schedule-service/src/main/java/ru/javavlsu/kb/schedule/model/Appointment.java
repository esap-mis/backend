package ru.javavlsu.kb.schedule.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.javavlsu.kb.schedule.validator.LocalTimeConstraint;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PatientRef patient;

    @ManyToOne(fetch = FetchType.LAZY)
    private DoctorRef doctor;

    private LocalDate date;

    @Column(name = "start_time")
    @LocalTimeConstraint(message = "Time should have minutes either 00 or 30")
    private LocalTime startAppointments;

    @Column(name = "end_time")
    @LocalTimeConstraint(message = "Time should have minutes either 00 or 30")
    private LocalTime endAppointments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    @JsonIgnore
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
