package com.example.probation.validation.validator

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueEmail
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NewEmailValidator(
    private val usersService: UsersService
) : ConstraintValidator<UniqueEmail, ChangeInfoDto> {

    private var message: String? = null

    override fun isValid(info: ChangeInfoDto, context: ConstraintValidatorContext): Boolean =
        usersService.checkUniqueNewEmail(info.email, info.id).apply {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("email").addConstraintViolation()
        }

    override fun initialize(uniqueEmail: UniqueEmail) {
        super.initialize(uniqueEmail)
        this.message = uniqueEmail.message
    }
}
