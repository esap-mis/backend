package ru.javavlsu.kb.esap.dto.MedicalCardDTO;

import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.model.Patient;

import java.util.List;

@Getter
@Setter
public class MedicalCardResponseDTO {

    private Long id;

    private List<MedicalRecordResponseDTO> medicalRecord;

    private PatientResponseDTO patient;

}
