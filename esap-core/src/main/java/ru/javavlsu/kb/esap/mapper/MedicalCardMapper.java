package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalCardResponseDTO;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordRequestDTO;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.MedicalRecord;

@Mapper(componentModel = "spring")
public interface MedicalCardMapper {
    MedicalRecord toMedicalRecordRequestDTO(MedicalRecordRequestDTO medicalRecordRequestDTO);
    MedicalCardResponseDTO toMedicalCard(MedicalCard medicalCard);
}
