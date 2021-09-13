package com.example.probation.validation.validator

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueUsername
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NewNameValidator(
    private val usersService: UsersService,
) : ConstraintValidator<UniqueUsername, ChangeInfoDto> {

    private var message: String? = null

    override fun isValid(info: ChangeInfoDto, context: ConstraintValidatorContext) =
        usersService.checkUniqueNewName(info.username, info.id).apply {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("username").addConstraintViolation()
        }

    override fun initialize(uniqueName: UniqueUsername) {
        super.initialize(uniqueName)
        this.message = uniqueName.message
    }
}
