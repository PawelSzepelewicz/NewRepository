package com.example.probation.service

import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.exception.TokenNotFoundException
import com.example.probation.exception.UserNotFoundByTokenException
import com.example.probation.repository.TokenRepository
import com.example.probation.service.impl.TokenServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSender

@ExtendWith(MockitoExtension::class)
class TokenServiceImplTest {
    @Mock
    lateinit var tokenRepository: TokenRepository
    @Mock
    lateinit var messages: MessageSource
    @Mock
    lateinit var mailSender: JavaMailSender
    private val host: String = "\${server.host}"
    lateinit var service: TokenServiceImpl

    @BeforeEach
    fun setup() {
        service = TokenServiceImpl(tokenRepository, messages, mailSender, host)
    }

    @Test
    fun findByToken() {
        val token = "4314cdd0-b067-4c0e-b0b5-3adb2c77e125"
        val wrongToken = "4314cdd0-b067-4c0e-b0b5-3adb2c77e126"
        val verificationToken = VerificationToken(token, User())
        Mockito.`when`(tokenRepository.findByToken(token)).thenReturn(verificationToken)
        service.findByToken(token).apply {
            assertEquals(this.token, token)
        }
            assertThrows<TokenNotFoundException> { service.findByToken(wrongToken) }
    }

    @Test
    fun getUserByToken() {
        val token = "4314cdd0-b067-4c0e-b0b5-3adb2c77e125"
        val user = User("Username")
        val verificationToken = VerificationToken(token, user)
        Mockito.`when`(tokenRepository.findByToken(token)).thenReturn(verificationToken)
        service.getUserByToken(token).apply {
            assertEquals(user.username, username)
        }
        val tokenWithNullUser = VerificationToken(token, null)
        Mockito.`when`(tokenRepository.findByToken(token)).thenReturn(tokenWithNullUser)
        assertThrows<UserNotFoundByTokenException> {
            service.getUserByToken(token)
        }
    }
}
