package com.example.probation.service.impl

import com.example.probation.core.dto.LogDto
import com.example.probation.exception.ForbiddenException
import com.example.probation.service.KafkaProducerService
import lombok.extern.slf4j.Slf4j
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*

@Slf4j
@Service
class KafkaProducerServiceImpl(
    private val detailsService: CustomUserDetailsService,
    private val kafkaTemplate: KafkaTemplate<String, LogDto>,
) : KafkaProducerService {
    override fun send(action: String) {
        val logDto = LogDto()
        try {
            logDto.username = detailsService.getCurrentUsername()
        } catch (e: ForbiddenException)
        logDto.action = action
        val cal = Calendar.getInstance()
        logDto.actionTime = Timestamp(cal.time.time)
        kafkaTemplate.send("spring-audit", logDto)
    }
}
