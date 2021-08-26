package com.example.probation.repository

import com.example.probation.ProbationApplication
import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.repository.TokenRepository
import com.example.probation.repository.UsersRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*

@Sql("/init.sql")
@ActiveProfiles("local")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ProbationApplication::class]
)
class TokenRepositoryTest(
    @Autowired
    private val repository: TokenRepository,
    @Autowired
    private val usersRepository: UsersRepository
) {
    @Test
    fun findByToken() {
        val wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een"
        val token = "902a8cf0-63a7-444f-a424-8960907e7eeb"
        val verificationToken: VerificationToken? = repository.findByToken(token)
        assertNotNull(verificationToken)
        assertEquals(verificationToken!!.token, token)
        assertNull(repository.findByToken(wrongToken))
    }

    @Test
    fun getByUser() {
        val user: User? = usersRepository.findByUsername("Admin")
        val token = "902a8cf0-63a7-444f-a424-8960907e7eeb"
        val verificationToken: VerificationToken? = repository.getByUser(user!!)
        assertNotNull(repository.getByUser(user))
        assertEquals(verificationToken!!.token, token)
    }
}