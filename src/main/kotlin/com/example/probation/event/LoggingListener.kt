package com.example.probation.event

import com.example.probation.core.enums.Actions
import com.example.probation.service.KafkaProducerService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class LoggingListener(
    private val kafkaService: KafkaProducerService,
) : ApplicationListener<OnLoggingCompleteEvent> {
    override fun onApplicationEvent(event: OnLoggingCompleteEvent) {
        event.user.username?.let { kafkaService.send(Actions.LOG_IN.action, it) }
    }
}
