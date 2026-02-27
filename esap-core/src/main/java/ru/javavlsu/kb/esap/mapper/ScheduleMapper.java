package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.esap.dto.ScheduleDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.ScheduleResponseDTO;
import ru.javavlsu.kb.esap.model.Schedule;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TimeSlotMapper.class, AppointmentMapper.class})
public interface ScheduleMapper {
    ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule);
    Schedule toSchedule(ScheduleDTO scheduleDTO);
    List<ScheduleResponseDTO> toScheduleResponseDTOList(List<Schedule> schedules);
}
