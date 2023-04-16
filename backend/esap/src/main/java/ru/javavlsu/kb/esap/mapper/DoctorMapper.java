package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.DoctorDTO;
import ru.javavlsu.kb.esap.model.Doctor;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DoctorMapper {
    private final ModelMapper modelMapper;

    public DoctorMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Doctor toDoctor(DoctorDTO doctorDTO){
        return modelMapper.map(doctorDTO, Doctor.class);
    }

    public DoctorDTO toDoctorDTO(Doctor doctor) {
        return modelMapper.map(doctor, DoctorDTO.class);
    }

    public List<DoctorDTO> toDoctorDTOList(List<Doctor> doctors) {
        return doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());
    }
}
