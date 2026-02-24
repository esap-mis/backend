package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.esap.dto.auth.ClinicRegistration;
import ru.javavlsu.kb.esap.model.Clinic;

@Mapper(componentModel = "spring")
public interface ClinicMapper {
    Clinic toClinic(ClinicRegistration clinicRegistration);
}
