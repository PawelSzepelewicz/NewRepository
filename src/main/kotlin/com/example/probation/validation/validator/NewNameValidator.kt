package com.example.probation.validation.validator

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueNewName
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NewNameValidator(
    private val usersService: UsersService,
) : ConstraintValidator<UniqueNewName, ChangeInfoDto> {

    private var message: String? = null

    override fun isValid(info: ChangeInfoDto, context: ConstraintValidatorContext) =
        usersService.checkUniqueNewName(info.username!!, info.id).let {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("username").addConstraintViolation()
            it
        }

    override fun initialize(uniqueName: UniqueNewName) {
        super.initialize(uniqueName)
        this.message = uniqueName.message
    }
}
