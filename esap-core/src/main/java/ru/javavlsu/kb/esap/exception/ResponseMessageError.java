package ru.javavlsu.kb.esap.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.util.List;

public abstract class ResponseMessageError {

    @Getter
    @Setter
    private String message;

    public ResponseMessageError(String message) {
        this.message = message;
    }

    public static String createErrorMsg(List<FieldError> errors){
        StringBuilder errorMsg = new StringBuilder();
        for(FieldError error: errors){
            errorMsg.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append(";");
        }
        return errorMsg.toString();
    }

}
