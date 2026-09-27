package ru.javavlsu.kb.schedule.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.schedule.dto.DoctorRefDTO;
import ru.javavlsu.kb.schedule.model.DoctorRef;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorRefMapper {

    DoctorRefDTO toDoctorRefDTO(DoctorRef doctor);

    List<DoctorRefDTO> toDoctorRefDTOList(List<DoctorRef> doctors);
}
