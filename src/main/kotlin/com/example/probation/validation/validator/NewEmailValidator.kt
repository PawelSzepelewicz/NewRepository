package com.example.probation.validation.validator

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueNewEmail
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NewEmailValidator(
    private val usersService: UsersService
) : ConstraintValidator<UniqueNewEmail, ChangeInfoDto> {

    private var message: String? = null

    override fun isValid(info: ChangeInfoDto, context: ConstraintValidatorContext): Boolean =
        usersService.checkUniqueNewEmail(info.email!!, info.id).let {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("email").addConstraintViolation()
            it
        }

    override fun initialize(uniqueEmail: UniqueNewEmail) {
        super.initialize(uniqueEmail)
        this.message = uniqueEmail.message
    }
}
