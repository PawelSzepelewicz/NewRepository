package com.example.probation.service.impl

import com.example.probation.core.dto.LogDto
import com.example.probation.service.KafkaProducerService
import lombok.extern.slf4j.Slf4j
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Slf4j
@Service
class KafkaProducerServiceImpl(
    private val kafkaTemplate: KafkaTemplate<String, LogDto>
) : KafkaProducerService {
    override fun send(action: String, username: String, objectName: String?) {
        LogDto(username, action, objectName, LocalDateTime.now()).let {
            kafkaTemplate.send("spring-audit", it)
        }
    }
}
