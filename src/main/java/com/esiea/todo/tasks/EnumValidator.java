package com.esiea.todo.tasks;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}