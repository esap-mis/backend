package ru.javavlsu.kb.esap.dto.MedicalCardDTO;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnalysisResponseDTO {

    private Long id;

    private String name;

    private String result;

    private LocalDateTime date;

}
