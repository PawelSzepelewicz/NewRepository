package com.example.probation.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TokenRepositoryTest(
    @Autowired
    private val repository: TokenRepository,
    @Autowired
    private val usersRepository: UsersRepository
) : RepositoryTest() {
    @Test
    fun findByToken() {
        val wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een"
        val token = "902a8cf0-63a7-444f-a424-8960907e7eeb"
        repository.findByToken(token)?.let {
            assertNotNull(it)
            assertEquals(it.token, token)
            assertNull(repository.findByToken(wrongToken))
        }
    }

    @Test
    fun getByUser() {
        val token = "902a8cf0-63a7-444f-a424-8960907e7eeb"
        usersRepository.findByUsername("Admin")?.let {
            repository.getByUser(it).apply {
                assertNotNull(repository.getByUser(it))
                assertEquals(token, token)
            }
        }
    }
}
