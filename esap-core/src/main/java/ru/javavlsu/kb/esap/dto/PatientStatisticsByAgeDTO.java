package ru.javavlsu.kb.esap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientStatisticsByAgeDTO {
    private int child;
    private int adult;
    private int elderly;
}

