package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.DoctorDTO;
import ru.javavlsu.kb.esap.dto.DoctorResponseDTO;
import ru.javavlsu.kb.esap.dto.auth.DoctorRegistration;
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

    public Doctor toDoctor(DoctorRegistration doctorRegistration){
        return modelMapper.map(doctorRegistration, Doctor.class);
    }

    public DoctorDTO toDoctorDTO(Doctor doctor) {
        return modelMapper.map(doctor, DoctorDTO.class);
    }

    public List<DoctorDTO> toDoctorDTOList(List<Doctor> doctors) {
        return doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());
    }

    public Page<DoctorDTO> toDoctorDTOPage(Page<Doctor> doctors) {
        List<DoctorDTO> doctorDTOList = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(doctorDTOList, doctors.getPageable(), doctors.getTotalElements());
    }
}
