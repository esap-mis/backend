package ru.javavlsu.kb.esap.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.ScheduleResponseDTO;
import ru.javavlsu.kb.esap.model.Clinic;

import java.util.List;

@Getter
@Setter
//TODO потестить @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DoctorDTO {
    //TODO потестить @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) - возможно уменьшиться количество dto
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String login;

    private String firstName;

    @Size(max = 100)
    private String patronymic;

    private String lastName;

    private String specialization;

    private int gender;

    private ClinicDTO clinic;

    private List<ScheduleResponseDTO> schedules;
}
