package com.example.probation.validatiion.validator;

import com.example.probation.service.UsersService;
import com.example.probation.validatiion.annotation.UniqueEmail;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class EmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UsersService usersService;

    @Override
    public void initialize(UniqueEmail uniqueEmail) {
        ConstraintValidator.super.initialize(uniqueEmail);
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null) {
            return false;
        }

        return !usersService.checkEmailExistence(email);
    }
}
