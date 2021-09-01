package com.example.probation.core.dto

import com.example.probation.validation.annotation.Password
import com.example.probation.validation.annotation.UniqueEmail
import com.example.probation.validation.annotation.UniqueUsername

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class CreateUserDto(
    @field:UniqueUsername
    @field:NotEmpty(message = "{name.notempty}")
    @field:Size(min = 3, max = 32, message = "{name.size}")
    var username: String? = null,
    @field:NotEmpty(message = "{description.notempty}")
    @field:Size(min = 1, max = 1000, message = "{description.size}")
    var description: String? = null,
    @field:NotBlank(message = "{password.notempty}")
    @field:Password
    var password: String? = null,
    @field:Email
    @field:NotBlank(message = "{email.notempty}")
    @field:UniqueEmail
    var email: String? = null
)
