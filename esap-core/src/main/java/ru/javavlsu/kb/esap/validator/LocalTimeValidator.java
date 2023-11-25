package ru.javavlsu.kb.esap.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;

public class LocalTimeValidator implements ConstraintValidator<LocalTimeConstraint, LocalTime> {

    @Override
    public boolean isValid(LocalTime localTime, ConstraintValidatorContext context) {
        if (localTime == null) {
            return true;
        }
        return localTime.getMinute() == 0 || localTime.getMinute() == 30;
    }
}