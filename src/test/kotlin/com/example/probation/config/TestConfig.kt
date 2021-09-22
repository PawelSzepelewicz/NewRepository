package com.example.probation.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
open class TestConfig(
    @Value("\${spring.mail.host}") private val host: String,
    @Value("\${spring.mail.port}") private val port: Int,
    @Value("\${spring.mail.username}") private val username: String,
    @Value("\${spring.mail.password}") private var password: String,
) {
    @Bean
    fun javaMailSender(): JavaMailSender? =
        JavaMailSenderImpl().apply {
            this.host = host
            this.port = port
            this.username = username
            this.password = password
            javaMailProperties.let {
                it["mail.transport.protocol"] = "smtp"
                it["mail.smtp.auth"] = "true"
                it["mail.smtp.starttls.enable"] = "true"
                it["mail.debug"] = "true"
            }
        }
}
