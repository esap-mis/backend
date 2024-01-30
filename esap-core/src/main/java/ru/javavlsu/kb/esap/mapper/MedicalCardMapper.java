package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalCardResponseDTO;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordRequestDTO;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordResponseDTO;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.MedicalRecord;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MedicalCardMapper {

    private final ModelMapper modelMapper;
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalCardMapper(ModelMapper modelMapper, MedicalRecordMapper medicalRecordMapper) {
        this.modelMapper = modelMapper;
        this.medicalRecordMapper = medicalRecordMapper;
    }

    public MedicalRecord toMedicalRecordRequestDTO(MedicalRecordRequestDTO medicalRecordRequestDTO){
        return modelMapper.map(medicalRecordRequestDTO, MedicalRecord.class);
    }

    public MedicalCardResponseDTO toMedicalCard(MedicalCard medicalCard) {
        MedicalCardResponseDTO medicalCardResponseDTO = modelMapper.map(medicalCard, MedicalCardResponseDTO.class);

        List<MedicalRecordResponseDTO> medicalRecordResponseDTOs = medicalCard.getMedicalRecord()
                .stream()
                .map(medicalRecordMapper::toMedicalRecordResponseDTO)
                .collect(Collectors.toList());

        medicalCardResponseDTO.setMedicalRecord(medicalRecordResponseDTOs);
        return medicalCardResponseDTO;
    }
}
