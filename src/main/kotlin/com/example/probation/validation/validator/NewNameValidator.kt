package com.example.probation.validation.validator

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.exception.ValidationNewDataException
import com.example.probation.service.UsersService
import com.example.probation.validation.annotation.UniqueNewName
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class NewNameValidator(private val usersService: UsersService
) : ConstraintValidator<UniqueNewName, ChangeInfoDto> {

    override fun isValid(info: ChangeInfoDto, context: ConstraintValidatorContext): Boolean {
        val isValid = usersService.checkUniqueNewName(info.username!!, info.id)
        isValidField(isValid)
        return true
    }

    override fun initialize(uniqueName: UniqueNewName) {
        super.initialize(uniqueName)
    }

    private fun isValidField(isValid: Boolean) {
        if (!isValid) throw ValidationNewDataException("message", "email")
    }
 }