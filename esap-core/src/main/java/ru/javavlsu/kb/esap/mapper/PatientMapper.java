package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.PatientDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.PatientResponseDTO;
import ru.javavlsu.kb.esap.model.Patient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PatientMapper {
    private final ModelMapper modelMapper;

    public PatientMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public PatientDTO toPatientDTO(Patient patient) {
        return modelMapper.map(patient, PatientDTO.class);
    }

    public PatientResponseDTO toPatientResponseDTO(Patient patient) {
        return modelMapper.map(patient, PatientResponseDTO.class);
    }

    public Patient toPatient(PatientDTO patientDTO) {
        return modelMapper.map(patientDTO, Patient.class);
    }

    public Patient toPatient(PatientResponseDTO patientResponseDTO) {
        return modelMapper.map(patientResponseDTO, Patient.class);
    }

    public List<PatientResponseDTO> toPatientResponseDTOList(List<Patient> patients) {
        return patients.stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<PatientResponseDTO> toPatientResponseDTOList(Page<Patient> patients) {
        return patients.stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDTO.class))
                .collect(Collectors.toList());
    }

    public Page<PatientResponseDTO> toPatientResponseDTOPage(Page<Patient> patients) {
        List<PatientResponseDTO> patientResponseDTOList = patients.stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(patientResponseDTOList, patients.getPageable(), patients.getTotalElements());
    }
}
