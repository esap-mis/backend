package ru.javavlsu.kb.esap.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = LocalTimeValidator.class)
@Documented
public @interface LocalTimeConstraint {

    String message() default "{LocalTime.invalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}