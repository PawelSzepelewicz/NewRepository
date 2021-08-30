package com.example.probation.validation.validator

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.exception.ValidationNewDataException
import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueNewEmail
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NewEmailValidator(private val usersService: UsersService
) : ConstraintValidator<UniqueNewEmail, ChangeInfoDto> {

    override fun isValid(info: ChangeInfoDto, context: ConstraintValidatorContext): Boolean {
        val isValid = usersService.checkUniqueNewEmail(info.email!!, info.id)
        isValidField(isValid)
        return true
    }

    override fun initialize(uniqueEmail: UniqueNewEmail) {
        super.initialize(uniqueEmail)
    }

    private fun isValidField(isValid: Boolean) {
        if (!isValid) throw ValidationNewDataException("message", "username")
    }
}
