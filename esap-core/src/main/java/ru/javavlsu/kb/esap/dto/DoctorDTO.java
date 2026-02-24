package ru.javavlsu.kb.esap.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.ScheduleResponseDTO;
import ru.javavlsu.kb.esap.model.Clinic;

import java.util.List;

//TODO потестить @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DoctorDTO (
    //TODO потестить @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) - возможно уменьшиться количество dto
    Long id,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String login,
    String firstName,
    @Size(max = 100)
    String patronymic,
    String lastName,
    String specialization,
    int gender,
    ClinicDTO clinic,
    List<ScheduleResponseDTO> schedules
) {}
