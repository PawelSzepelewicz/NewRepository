package com.example.probation.core.dto

import java.util.*
import javax.validation.constraints.NotNull

data class LogDto(
    @NotNull
    var username: String? = null,
    @NotNull
    var message: String? = null,
    @NotNull
    var action: String? = null,
    @NotNull
    var actionTime: Date? = null
)
