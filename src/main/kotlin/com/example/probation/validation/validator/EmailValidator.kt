package com.example.probation.validation.validator

import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueEmail

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class EmailValidator(
    private val usersService: UsersService
) : ConstraintValidator<UniqueEmail, String> {
    override fun isValid(email: String?, context: ConstraintValidatorContext) = if (email == null) {
        false
    } else {
        !usersService.checkEmailExistence(email)
    }

    override fun initialize(uniqueEmail: UniqueEmail) {
        super.initialize(uniqueEmail)
    }
}
