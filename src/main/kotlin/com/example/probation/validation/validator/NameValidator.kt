package com.example.probation.validation.validator

import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueUsername

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NameValidator(
    private val usersService: UsersService
    ) : ConstraintValidator<UniqueUsername, String> {

    override fun isValid(name: String?, context: ConstraintValidatorContext) =
        if (name == null) {
            false
        } else !usersService.checkUsernameExistence(name)

    override fun initialize(uniqueUsername: UniqueUsername) {
        super.initialize(uniqueUsername)
    }
}
