package com.example.probation.repository

import com.example.probation.ProbationApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

@Sql("/init.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ProbationApplication::class]
)
class UsersRepositoryTest(
    @Autowired
    private var repository: UsersRepository
) {
    @Test
    @Throws(Exception::class)
    fun findById() {
        val userA = repository.findById(1L)
        val userB = repository.findById(2L)
        Assertions.assertTrue(userA.isPresent)
        Assertions.assertTrue(userB.isPresent)
        Assertions.assertTrue(userA.get().username!!.contains("Admin"))
        Assertions.assertTrue(userB.get().username!!.contains("User"))
    }

    @Test
    fun getRandomUsers() {
        val random = repository.getRandomUsers()
        Assertions.assertNotEquals(random[0], random[1])
        assertEquals(2, random.size)
    }

    @Test
    fun findAllByOrderByRatingDesc() {
        val userList = repository.findAllByOrderByRatingDesc()
        val ratings: MutableList<Int> = ArrayList()
        userList.forEach(Consumer { (_, _, _, rating) ->
            ratings.add(
                rating
            )
        })
        assertEquals(
            ratings.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()),
            ratings
        )
    }

    @Test
    fun findByUsername() {
        val username = "Next"
        assertEquals(repository.findByUsername(username)!!.username, username)
    }

    @Test
    fun findByEmail() {
        val email = "next@gmail.com"
        assertEquals(repository.findByEmail(email)!!.email, email)
    }
}
