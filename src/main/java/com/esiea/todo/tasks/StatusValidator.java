package com.esiea.todo.tasks;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<ValidStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        if (value.isEmpty()) {
            return false;
        }
        for (Status status : Status.values()) {
            if (status.name().equals(value)) {
                return true;
            }
        }

        return false;
    }
}