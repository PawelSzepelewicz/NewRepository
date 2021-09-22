package com.example.probation.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.Collections
import java.util.stream.Collectors


class UsersRepositoryTest(
    @Autowired
    private var repository: UsersRepository
) : RepositoryTest() {
    @Test
    @Throws(Exception::class)
    fun findById() {
        repository.findById(1L).apply {
            Assertions.assertTrue(isPresent)
            get().username?.let { Assertions.assertTrue(it.contains("Admin")) }
        }
        repository.findById(2L).apply {
            Assertions.assertTrue(isPresent)
            get().username?.let { Assertions.assertTrue(it.contains("User")) }
        }
    }

    @Test
    fun getRandomUsers() {
        repository.getRandomUsers().apply {
            Assertions.assertNotEquals(get(0), get(1))
            assertEquals(2, size)
        }
    }

    @Test
    fun findAllByOrderByRatingDesc() {
        val ratings: MutableList<Int> = ArrayList()
        repository.findAllByOrderByRatingDesc().forEach { user ->
            ratings.add(user.rating)
        }
        assertEquals(
            ratings.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()),
            ratings
        )
    }

    @Test
    fun findByUsername() {
        val username = "Next"
        assertEquals(repository.findByUsername(username)?.username, username)
    }

    @Test
    fun findByEmail() {
        val email = "next@gmail.com"
        assertEquals(repository.findByEmail(email)?.email, email)
    }
}
