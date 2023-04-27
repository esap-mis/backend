package ru.javavlsu.kb.esap.dto.MedicalCardDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.model.Analysis;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MedicalRecordRequestDTO {

    private String record;

    @NotBlank
    @NotNull
    private String fioAndSpecializationDoctor;

    private LocalDate date;

    private List<Analysis> analyzes;

}
