package com.example.probation.validatiion.validator;

import com.example.probation.service.UsersService;
import com.example.probation.validatiion.annotation.UniqueUsername;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class NameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final UsersService usersService;

    @Override
    public void initialize(UniqueUsername uniqueName) {
        ConstraintValidator.super.initialize(uniqueName);
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return false;
        }

        return !usersService.checkUsernameExistence(name);
    }
}
