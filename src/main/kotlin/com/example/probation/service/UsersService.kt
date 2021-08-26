package com.example.probation.service

import com.example.probation.core.entity.User
import java.util.*

interface UsersService {
    fun registerNewUser(newUser: User): User

    fun redefineRating(winner: User, loser: User)

    fun getUsersForComparison(): List<User>

    fun getTopUsersByRating(): List<User>

    fun calculateWinnerRating(currentRating: Int): Int

    fun calculateLoserRating(currentRating: Int): Int

    fun findByUserName(username: String): User?

    fun getUserByToken(token: String): User?

    fun saveRegisteredUser(token: String): User

    fun getCurrentUser(): User

    fun checkEmailExistence(email: String): Boolean

    fun checkUsernameExistence(username: String): Boolean
}