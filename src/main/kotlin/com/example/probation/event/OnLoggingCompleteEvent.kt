package com.example.probation.event

import com.example.probation.core.dto.UserDetailsDto
import org.springframework.context.ApplicationEvent

class OnLoggingCompleteEvent(
    var user: UserDetailsDto
) : ApplicationEvent(Any())
