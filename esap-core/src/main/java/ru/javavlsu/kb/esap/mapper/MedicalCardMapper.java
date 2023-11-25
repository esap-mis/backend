package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalCardResponseDTO;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordRequestDTO;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.MedicalRecord;

@Component
public class MedicalCardMapper {

    private final ModelMapper modelMapper;

    public MedicalCardMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public MedicalRecord toMedicalRecordRequestDTO(MedicalRecordRequestDTO medicalRecordRequestDTO){
        return modelMapper.map(medicalRecordRequestDTO, MedicalRecord.class);
    }

    public MedicalCardResponseDTO toMedicalCard(MedicalCard medicalCard) {
        return modelMapper.map(medicalCard, MedicalCardResponseDTO.class);
    }
}
