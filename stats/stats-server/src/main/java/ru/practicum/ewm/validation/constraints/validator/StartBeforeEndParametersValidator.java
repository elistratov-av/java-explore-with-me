package ru.practicum.ewm.validation.constraints.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import ru.practicum.ewm.validation.constraints.StartBeforeEndParameters;

import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class StartBeforeEndParametersValidator
        implements ConstraintValidator<StartBeforeEndParameters, Object[]> {

    @Override
    public boolean isValid(Object[] value, ConstraintValidatorContext context) {
        if (value[0] == null || value[1] == null) {
            return true;
        }

        if (!(value[0] instanceof LocalDateTime)
                || !(value[1] instanceof LocalDateTime)) {
            throw new IllegalArgumentException(
                    "Недопустимая сигнатура метода, ожидаются два параметра типа LocalDateTime");
        }

        return ((LocalDateTime) value[0]).isBefore((LocalDateTime) value[1]);
    }
}
