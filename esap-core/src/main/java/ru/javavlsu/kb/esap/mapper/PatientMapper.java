package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.javavlsu.kb.esap.dto.PatientDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.PatientResponseDTO;
import ru.javavlsu.kb.esap.model.Patient;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientDTO toPatientDTO(Patient patient);
    PatientResponseDTO toPatientResponseDTO(Patient patient);
    Patient toPatient(PatientDTO patientDTO);
    Patient toPatient(PatientResponseDTO patientResponseDTO);
    List<PatientResponseDTO> toPatientResponseDTOList(List<Patient> patients);
    List<PatientResponseDTO> toPatientResponseDTOList(Page<Patient> patients);
    default Page<PatientResponseDTO> toPatientResponseDTOPage(Page<Patient> patients) {
        return patients.map(this::toPatientResponseDTO);
    }
}