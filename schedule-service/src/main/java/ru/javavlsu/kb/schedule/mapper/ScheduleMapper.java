package ru.javavlsu.kb.schedule.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.javavlsu.kb.schedule.dto.ScheduleDTO;
import ru.javavlsu.kb.schedule.dto.ScheduleResponseDTO;
import ru.javavlsu.kb.schedule.model.Schedule;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule);

    /**
     * id, doctor, maxPatientPerDay и appointments выставляет ScheduleService:
     * они зависят от проверок и вычислений, которых нет в DTO.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "maxPatientPerDay", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    Schedule toSchedule(ScheduleDTO scheduleDTO);

    default List<ScheduleResponseDTO> toScheduleResponseDTOList(List<Schedule> schedules) {
        return schedules.stream().map(this::toScheduleResponseDTO).toList();
    }
}
