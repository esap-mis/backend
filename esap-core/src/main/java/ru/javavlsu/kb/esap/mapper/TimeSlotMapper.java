package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.TimeSlotResponseDTO;
import ru.javavlsu.kb.esap.model.TimeSlot;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TimeSlotMapper {
    TimeSlotResponseDTO toTimeSlotResponseDTO(TimeSlot timeSlot);
    List<TimeSlotResponseDTO> toTimeSlotResponseDTOList(List<TimeSlot> timeSlots);
}
