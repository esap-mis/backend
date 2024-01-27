package ru.javavlsu.kb.esap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "medical_record")
public class MedicalRecord implements Attachable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record")
    private String record;

    @Column(name = "doctor")
    @NotBlank
    @NotNull
    private String fioAndSpecializationDoctor;

    @Column(name = "date")
    @NotNull
    private LocalDate date;

    @OneToMany(mappedBy = "medicalRecord", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Analysis> analyzes;

    @ManyToOne
    @JoinColumn(name = "medical_card_id", referencedColumnName = "id")
    @NotNull
    @JsonIgnore
    private MedicalCard medicalCard;

    @OneToMany(mappedBy = "attachment", cascade = CascadeType.ALL)
    private List<Document> documents;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int sortByDateDesc(MedicalRecord otherMedicalRecord){
        return date.compareTo(otherMedicalRecord.date) * -1;
    }

}
