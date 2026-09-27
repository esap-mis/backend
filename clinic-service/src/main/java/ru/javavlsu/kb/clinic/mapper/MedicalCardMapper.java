package ru.javavlsu.kb.clinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.javavlsu.kb.clinic.dto.AnalysisRequestDTO;
import ru.javavlsu.kb.clinic.dto.AnalysisResponseDTO;
import ru.javavlsu.kb.clinic.dto.MedicalCardResponseDTO;
import ru.javavlsu.kb.clinic.dto.MedicalRecordRequestDTO;
import ru.javavlsu.kb.clinic.dto.MedicalRecordResponseDTO;
import ru.javavlsu.kb.clinic.dto.PatientRefDTO;
import ru.javavlsu.kb.clinic.model.Analysis;
import ru.javavlsu.kb.clinic.model.MedicalCard;
import ru.javavlsu.kb.clinic.model.MedicalRecord;
import ru.javavlsu.kb.clinic.model.PatientRef;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicalCardMapper {

    /**
     * id, date, medicalCard, analyzes и fioAndSpecializationDoctor заполняет
     * MedicalCardService: дата подставляется по умолчанию, подпись врача берётся
     * из read-модели, а владелец записи известен только на сервисе.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "medicalCard", ignore = true)
    @Mapping(target = "analyzes", ignore = true)
    @Mapping(target = "fioAndSpecializationDoctor", ignore = true)
    MedicalRecord toMedicalRecord(MedicalRecordRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medicalRecord", ignore = true)
    @Mapping(target = "date", ignore = true)
    Analysis toAnalysis(AnalysisRequestDTO dto);

    AnalysisResponseDTO toAnalysisResponseDTO(Analysis analysis);

    MedicalCardResponseDTO toMedicalCard(MedicalCard medicalCard);

    MedicalRecordResponseDTO toMedicalRecordResponse(MedicalRecord record);

    default List<MedicalRecordResponseDTO> toMedicalRecordResponseList(List<MedicalRecord> records) {
        return records.stream().map(this::toMedicalRecordResponse).toList();
    }

    PatientRefDTO toPatientRefDTO(PatientRef patient);
}
