package ru.javavlsu.kb.esap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "analyzes")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "result")
    private String result;

    @Column(name = "date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    @JsonIgnore
    @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    private MedicalRecord medicalRecord;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analysis analysis = (Analysis) o;
        return Objects.equals(id, analysis.id) && Objects.equals(name, analysis.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

