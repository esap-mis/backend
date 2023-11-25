package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.ScheduleDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.ScheduleResponseDTO;
import ru.javavlsu.kb.esap.model.Schedule;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduleMapper {

    private final ModelMapper modelMapper;

    public ScheduleMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ScheduleResponseDTO toScheduleResponseDTO(Schedule schedule) {
        return modelMapper.map(schedule, ScheduleResponseDTO.class);
    }

    public Schedule toSchedule(ScheduleDTO scheduleDTO) {
        return modelMapper.map(scheduleDTO, Schedule.class);
    }

    public List<ScheduleResponseDTO> toScheduleResponseDTOList(List<Schedule> schedules) {
        return schedules.stream()
                .map(schedule -> modelMapper.map(schedule, ScheduleResponseDTO.class))
                .collect(Collectors.toList());
    }
}
