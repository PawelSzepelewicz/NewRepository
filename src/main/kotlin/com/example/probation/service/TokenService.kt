package com.example.probation.service

import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken

interface TokenService {
    fun saveNewToken(token: VerificationToken)

    fun findByToken(token: String): VerificationToken?

    fun getUserByToken(token: String): User?

    fun confirmRegistration(user: User)

    fun deleteTokenByUser(user: User)
}
