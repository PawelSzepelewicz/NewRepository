package com.example.probation.service.impl

import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.core.enums.Actions
import com.example.probation.exception.TokenNotFoundException
import com.example.probation.repository.TokenRepository
import com.example.probation.service.KafkaProducerService
import com.example.probation.service.TokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenServiceImpl(
    private val tokenRepository: TokenRepository,
    private val messages: MessageSource,
    private val mailSender: JavaMailSender,
    private val producer: KafkaProducerService,
    @Value("\${server.host}") private val host: String) : TokenService {

    override fun saveNewToken(token: VerificationToken) {
        tokenRepository.save(token)
    }

    override fun findByToken(token: String): VerificationToken =
        tokenRepository.findByToken(token) ?: throw TokenNotFoundException("token.notfound")

    override fun getUserByToken(token: String): User =
        findByToken(token).user!!

    override fun confirmRegistration(user: User) {
        val token = UUID.randomUUID().toString()
        saveNewToken(VerificationToken(token, user))
        val recipientAddress = user.email
        val subject = "Registration Confirmation"
        val endpoint = "/confirmation?token="
        val confirmationUrl = host + endpoint + token
        val messageText = "message.registrationSuccess"
        val message = messages.getMessage(messageText, null, LocaleContextHolder.getLocale())
        val email = SimpleMailMessage()
        email.setTo(recipientAddress)
        email.setSubject(subject)
        email.setText(
            """
                $message
                $confirmationUrl
                """.trimIndent()
        )
        mailSender.send(email)
        producer.send(Actions.REGISTER.action)
    }
}
