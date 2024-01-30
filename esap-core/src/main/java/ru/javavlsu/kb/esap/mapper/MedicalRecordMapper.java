package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordResponseDTO;
import ru.javavlsu.kb.esap.model.Document;
import ru.javavlsu.kb.esap.model.MedicalRecord;
import ru.javavlsu.kb.esap.repository.DocumentRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class MedicalRecordMapper {

    private final ModelMapper modelMapper;
    private final DocumentRepository documentRepository;

    public MedicalRecordMapper(ModelMapper modelMapper, DocumentRepository documentRepository) {
        this.modelMapper = modelMapper;
        this.documentRepository = documentRepository;
    }

    public MedicalRecordResponseDTO toMedicalRecordResponseDTO(MedicalRecord medicalRecord) {
        List<Document> documents = documentRepository.findDocumentsByMedicalRecord(medicalRecord);
        MedicalRecordResponseDTO medicalRecordResponseDTO = modelMapper.map(medicalRecord, MedicalRecordResponseDTO.class);
        List<String> filesUrls = new ArrayList<>();
        for (Document document: documents) {
            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("api/file/download/" + document.getId())
                    .toUriString();
            filesUrls.add(downloadUrl);
        }
        medicalRecordResponseDTO.setDocuments(filesUrls);
        return  medicalRecordResponseDTO;
    }

    public MedicalRecord toMedicalRecord(MedicalRecordResponseDTO medicalRecordResponseDTO) {
        return modelMapper.map(medicalRecordResponseDTO, MedicalRecord.class);
    }
}