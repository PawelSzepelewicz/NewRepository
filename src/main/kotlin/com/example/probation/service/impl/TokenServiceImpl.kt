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
import java.util.UUID

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
            val confirmationUrl = "${host}/confirmation?token=${token}"
            val message = messages.getMessage("message.registrationSuccess", null, LocaleContextHolder.getLocale())
            SimpleMailMessage().apply {
                setTo(user.email)
                setSubject("token.message.subject")
                setText("$message\n$confirmationUrl")
            }.also { mailSender.send(it) }
        }
    }

    override fun deleteTokenByUser(user: User) {
        tokenRepository.getByUser(user)?.let {
            tokenRepository.delete(it)
        }
    }
}
