package ru.javavlsu.kb.auth.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.javavlsu.kb.auth.dto.PatientDTO;
import ru.javavlsu.kb.auth.dto.PatientResponseDTO;
import ru.javavlsu.kb.auth.model.Patient;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "login", ignore = true)
    @org.mapstruct.Mapping(target = "password", ignore = true)
    @org.mapstruct.Mapping(target = "role", ignore = true)
    @org.mapstruct.Mapping(target = "clinic", ignore = true)
    Patient toPatient(PatientDTO patientDTO);

    PatientDTO toPatientDTO(Patient patient);

    PatientResponseDTO toPatientResponseDTO(Patient patient);

    List<PatientResponseDTO> toPatientResponseDTOList(List<Patient> patients);

    default Page<PatientResponseDTO> toPatientResponseDTOPage(Page<Patient> patients) {
        return patients.map(this::toPatientResponseDTO);
    }
}
