package ru.practicum.ewm.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.practicum.ewm.validation.constraints.validator.StartBeforeEndParametersValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = StartBeforeEndParametersValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface StartBeforeEndParameters {

    String message() default
            "Дата окончания должна быть после даты начала";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
