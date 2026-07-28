package ru.javavlsu.kb.esap.dto.MedicalCardDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class MedicalRecordResponseDTO {

    private Long id;

    private String record;

    private String fioAndSpecializationDoctor;

    private LocalDate date;

    private List<AnalysisResponseDTO> analyzes;

}
