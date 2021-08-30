package com.example.probation.repository

import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import org.springframework.data.jpa.repository.JpaRepository

interface TokenRepository : JpaRepository<VerificationToken, Long> {
    fun findByToken(token: String): VerificationToken?

    fun getByUser(user: User): VerificationToken?

    fun deleteVerificationTokenByUser(user: User)
}
