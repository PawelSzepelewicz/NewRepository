package com.example.probation.core.dto

import com.example.probation.validation.annotation.Password
import javax.validation.constraints.NotBlank

data class ChangePasswordDto(
    var id: Long,
    var oldPassword: String? = null,
    @field:NotBlank(message = "{password.notempty}")
    @field:Password
    var newPassword: String? = null
)
