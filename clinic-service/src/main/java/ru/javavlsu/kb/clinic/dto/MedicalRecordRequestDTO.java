package ru.javavlsu.kb.clinic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordRequestDTO {

    private String record;

    private LocalDate date;

    @NotBlank
    @NotNull
    private String fioAndSpecializationDoctor;

    private List<AnalysisRequestDTO> analyzes;
}
