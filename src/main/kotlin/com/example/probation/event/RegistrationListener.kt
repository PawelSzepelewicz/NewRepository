package com.example.probation.event

import com.example.probation.service.TokenService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class RegistrationListener(
    private val tokenService: TokenService
) : ApplicationListener<OnRegistrationCompleteEvent> {

    override fun onApplicationEvent(event: OnRegistrationCompleteEvent) {
        confirmRegistration(event)
    }

    fun confirmRegistration(event: OnRegistrationCompleteEvent) {
        event.user?.let {
            tokenService.confirmRegistration(it)
        }
    }
}
