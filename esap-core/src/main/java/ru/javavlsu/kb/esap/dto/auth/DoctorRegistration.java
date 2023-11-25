package ru.javavlsu.kb.esap.dto.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.ScheduleResponseDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorRegistration extends UserRegistration {

    private String firstName;

    @Size(max = 100)
    private String patronymic;

    private String lastName;

    private String specialization;

    @Max(value = 2, message = "Не верно указан пол")
    @Min(value = 1, message = "Не верно указан пол")
    private int gender;

    public DoctorRegistration(Long id, String login, String password, String firstName, String patronymic, String lastName, String specialization, int gender, String role) {
        super(id, password, login, role);
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.lastName = lastName;
        this.specialization = specialization;
        this.gender = gender;
    }
}
