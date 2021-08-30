package com.example.probation.service

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.entity.User

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

    fun blockUser(userId: Long)

    fun unblockUser(userId: Long)

    fun deleteUser(userId: Long)

    fun changePersonalData(id: Long, user: User)

    fun changePassword(changePasswordDto: ChangePasswordDto)

    fun checkUniqueNewName(newName: String, id: Long): Boolean

    fun checkUniqueNewEmail(newEmail: String, id: Long): Boolean
}