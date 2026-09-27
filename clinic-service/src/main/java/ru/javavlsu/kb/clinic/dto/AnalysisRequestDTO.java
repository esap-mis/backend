package ru.javavlsu.kb.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO анализа на входе. Раньше здесь просачивалась JPA-сущность Analysis —
 * теперь контракт наружу не протекает.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisRequestDTO {

    private String name;

    private String result;
}
