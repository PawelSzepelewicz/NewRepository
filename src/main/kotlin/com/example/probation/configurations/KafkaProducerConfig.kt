package com.example.probation.configurations

import com.example.probation.core.dto.LogDto
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@EnableKafka
@Configuration
open class KafkaProducerConfig(
    @Value("\${kafka.server}") private val kafkaServer: String,
    @Value("\${kafka.group.id}") private val kafkaProducerId: String
) {
    @Bean
    open fun producerFactory(): ProducerFactory<String, LogDto> {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaServer
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        props[ProducerConfig.CLIENT_ID_CONFIG] = kafkaProducerId
        return DefaultKafkaProducerFactory(props)
    }

    @Bean
    open fun kafkaTemplate(): KafkaTemplate<String, LogDto> {
        return KafkaTemplate(producerFactory())
    }
}
