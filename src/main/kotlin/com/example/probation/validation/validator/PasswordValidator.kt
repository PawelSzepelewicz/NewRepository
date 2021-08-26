package com.example.probation.validation.validator

import com.example.probation.validation.annotation.Password
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class PasswordValidator() :
    ConstraintValidator<Password, String> {
    override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
        return password?.matches("""(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,}$""".toRegex()) ?: false
    }

    override fun initialize(password: Password) {
        super.initialize(password)
    }
}
