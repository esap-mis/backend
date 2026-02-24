package ru.javavlsu.kb.esap.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public record AuthenticationDTO (
        @Size(min = 3, max = 255, message = "login должен быть от 3 до 255 символов")
        @NotBlank
        @NotNull
        String login,
        @NotBlank
        @NotNull
        String password
) {}
