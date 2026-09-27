package ru.javavlsu.kb.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.javavlsu.kb.auth.dto.ClinicDTO;
import ru.javavlsu.kb.auth.dto.ClinicRegistration;
import ru.javavlsu.kb.auth.model.Clinic;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClinicMapper {

    @Mapping(target = "id", ignore = true)
    Clinic toClinic(ClinicRegistration clinicRegistration);

    ClinicDTO toClinicDTO(Clinic clinic);

    default List<ClinicDTO> toClinicDTOList(List<Clinic> clinics) {
        return clinics.stream().map(this::toClinicDTO).toList();
    }
}
