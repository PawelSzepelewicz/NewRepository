package com.example.probation.event

import com.example.probation.core.entity.User
import org.springframework.context.ApplicationEvent

class OnRegistrationCompleteEvent(
    var user: User? = null
) : ApplicationEvent(Any()) {
}
