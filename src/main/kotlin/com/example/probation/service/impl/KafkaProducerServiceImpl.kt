package com.example.probation.service.impl

import com.example.probation.core.dto.LogDto
import com.example.probation.service.KafkaProducerService
import lombok.extern.slf4j.Slf4j
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.Calendar


@Slf4j
@Service
class KafkaProducerServiceImpl(
    private val kafkaTemplate: KafkaTemplate<String, LogDto>
) : KafkaProducerService {
    override fun send(action: String, username: String, subject: String?) {
        LogDto().apply {
            this.username = username
            this.action = action
            val cal = Calendar.getInstance()
            this.subject = subject
            this.actionTime = Timestamp(cal.time.time)
            kafkaTemplate.send("spring-audit", this)
        }
    }
}
