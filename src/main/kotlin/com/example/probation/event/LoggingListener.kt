package com.example.probation.event

import com.example.probation.core.enums.Actions
import com.example.probation.service.KafkaProducerService
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class LoggingListener(
    private val kafkaProducerService: KafkaProducerService,
) : ApplicationListener<OnLoggingCompleteEvent> {
    override fun onApplicationEvent(event: OnLoggingCompleteEvent) {
       kafkaProducerService.send(Actions.LOG_IN.action, event.user.username, null)
    }
}
