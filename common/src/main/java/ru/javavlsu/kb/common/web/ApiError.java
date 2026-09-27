package ru.javavlsu.kb.common.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Единый формат ошибки во всех сервисах.
 */
@Getter
@Setter
@AllArgsConstructor
public class ApiError {

    private int code;

    private HttpStatus status;

    private String message;
}
