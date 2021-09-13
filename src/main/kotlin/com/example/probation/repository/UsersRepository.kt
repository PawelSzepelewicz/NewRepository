package com.example.probation.repository

import com.example.probation.core.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UsersRepository : JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users ORDER BY RAND() LIMIT 2", nativeQuery = true)
    fun getRandomUsers(): List<User>

    fun findAllByOrderByRatingDesc(): List<User>

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?
}
