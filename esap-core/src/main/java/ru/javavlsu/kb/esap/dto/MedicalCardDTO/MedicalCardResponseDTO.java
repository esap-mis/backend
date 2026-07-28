package ru.javavlsu.kb.esap.dto.MedicalCardDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MedicalCardResponseDTO {

    private Long id;

    private List<MedicalRecordResponseDTO> medicalRecord;

    private PatientResponseDTO patient;

}
