package ru.javavlsu.kb.esap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.javavlsu.kb.esap.validator.LocalTimeConstraint;

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
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    @Column(name = "start_time")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @LocalTimeConstraint(message = "Time should have minutes either 00 or 30")
    private LocalTime startAppointments;

    @Column(name = "end_time")
    @JsonSerialize(using = LocalTimeSerializer.class)
    @LocalTimeConstraint(message = "Time should have minutes either 00 or 30")
    private LocalTime endAppointments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    @JsonIgnore
    private Schedule schedule;

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