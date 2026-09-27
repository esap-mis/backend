package ru.javavlsu.kb.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthenticationDTO(
        @Size(min = 3, max = 255, message = "login должен быть от 3 до 255 символов")
        @NotBlank @NotNull String login,
        @NotBlank @NotNull String password
) {
}
