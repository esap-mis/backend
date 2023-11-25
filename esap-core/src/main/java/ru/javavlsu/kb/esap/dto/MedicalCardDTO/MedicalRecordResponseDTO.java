package ru.javavlsu.kb.esap.dto.MedicalCardDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MedicalRecordResponseDTO {

    private Long id;

    private String record;

    private String fioAndSpecializationDoctor;

    private LocalDate date;

    private List<AnalysisResponseDTO> analyzes;

}
