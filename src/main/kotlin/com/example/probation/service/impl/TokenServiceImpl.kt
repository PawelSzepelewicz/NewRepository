package com.example.probation.service.impl

import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.exception.TokenNotFoundException
import com.example.probation.exception.UserNotFoundByTokenException
import com.example.probation.repository.TokenRepository
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
    @Value("\${server.host}") private val host: String
) : TokenService {

    override fun saveNewToken(token: VerificationToken) {
        tokenRepository.save(token)
    }

    override fun findByToken(token: String) =
        tokenRepository.findByToken(token) ?: throw TokenNotFoundException("token.notfound")

    override fun getUserByToken(token: String) =
        findByToken(token).user ?: throw UserNotFoundByTokenException("user.bytoken.notfound")

    override fun confirmRegistration(user: User) {
        UUID.randomUUID().toString().let { token ->
            saveNewToken(VerificationToken(token, user))
            val recipientAddress = user.email
            val subject = "Registration Confirmation"
            val endpoint = "/confirmation?token="
            val confirmationUrl = host + endpoint + token
            val messageText = "message.registrationSuccess"
            val message = messages.getMessage(messageText, null, LocaleContextHolder.getLocale())
            SimpleMailMessage().apply {
                setTo(recipientAddress)
                setSubject(subject)
                setText("$message\n$confirmationUrl")
            }.also {mailSender.send(it)}
        }
    }

    override fun deleteTokenByUser(user: User) {
        tokenRepository.getByUser(user).let {
            if (it != null) {
                tokenRepository.delete(it)
            }
        }
    }
}
