package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.auth.ClinicRegistration;
import ru.javavlsu.kb.esap.model.Clinic;

@Component
public class ClinicMapper {

    private final ModelMapper modelMapper;

    public ClinicMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Clinic toClinic(ClinicRegistration clinicRegistration){
        return modelMapper.map(clinicRegistration, Clinic.class);
    }
}
