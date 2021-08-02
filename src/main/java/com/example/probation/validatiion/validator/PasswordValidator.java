package com.example.probation.validatiion.validator;

import com.example.probation.validatiion.annotation.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    @Override
    public void initialize(com.example.probation.validatiion.annotation.Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) {
            return true;
        }

        return password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,}$");
    }
}
