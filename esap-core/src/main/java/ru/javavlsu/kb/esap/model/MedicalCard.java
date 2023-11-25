package ru.javavlsu.kb.esap.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "medical_card")
public class MedicalCard {

    public MedicalCard() {
    }

    public MedicalCard(Patient patient) {
        this.patient = patient;
    }

    public MedicalCard(List<MedicalRecord> medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record")
    @OneToMany(mappedBy = "medicalCard", fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
//    @Fetch(FetchMode.SUBSELECT)
    private List<MedicalRecord> medicalRecord;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalCard that = (MedicalCard) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

