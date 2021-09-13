package com.example.probation.core.dto

import com.example.probation.validation.annotation.UniqueEmail
import com.example.probation.validation.annotation.UniqueUsername
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@UniqueUsername
@UniqueEmail
data class ChangeInfoDto(
    val id: Long,
    @field:NotEmpty(message = "{name.notempty}")
    @field:Size(min = 3, max = 32, message = "{name.size}")
    var username: String,
    @field:NotEmpty(message = "{description.notempty}")
    @field:Size(min = 1, max = 1000, message = "{description.size}")
    var description: String? = null,
    @field:Email
    @field:NotBlank(message = "{email.notempty}")
    var email: String
)
