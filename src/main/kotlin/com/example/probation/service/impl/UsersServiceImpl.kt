package com.example.probation.service.impl

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.event.OnRegistrationCompleteEvent
import com.example.probation.exception.EntityNotFoundException
import com.example.probation.exception.ForbiddenException
import com.example.probation.exception.NoSuchUserException
import com.example.probation.exception.PasswordDoesNotMatchesException
import com.example.probation.exception.RoleNotFoundException
import com.example.probation.exception.TimeHasExpiredException
import com.example.probation.exception.UserNotFoundByTokenException
import com.example.probation.repository.UsersRepository
import com.example.probation.service.RoleService
import com.example.probation.service.TokenService
import com.example.probation.service.UsersService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService,
    private val tokenService: TokenService,
    private val detailsService: CustomUserDetailsService,
    private val eventPublisher: ApplicationEventPublisher,
) : UsersService {
    companion object {
        const val ADDITION = 15
    }

    override fun registerNewUser(newUser: User): User {
        val userRole = "USER"
        newUser.roles = mutableSetOf(
            roleService.getRoleByRoleName(userRole) ?: throw RoleNotFoundException("role.notfound")
        )
        newUser.password = passwordEncoder.encode(newUser.password)
        eventPublisher.publishEvent(OnRegistrationCompleteEvent(newUser))
        return usersRepository.save(newUser)
    }

    override fun redefineRating(winner: User, loser: User) {
        val players: MutableList<User> = ArrayList()
        loser.rating = calculateLoserRating(loser.rating)
        winner.rating = calculateWinnerRating(winner.rating)
        players.add(winner)
        players.add(loser)
        usersRepository.saveAll(players)
    }

    override fun getUsersForComparison(): List<User> =
        usersRepository.getRandomUsers()

    override fun getTopUsersByRating(): List<User> =
        usersRepository.findAllByOrderByRatingDesc();

    override fun calculateWinnerRating(currentRating: Int): Int = currentRating + ADDITION

    override fun calculateLoserRating(currentRating: Int): Int = currentRating - ADDITION

    override fun findByUserName(username: String): User =
        usersRepository.findByUsername(username) ?: throw throw ForbiddenException("{forbidden}")

    override fun getUserByToken(token: String): User? =
        tokenService.findByToken(token)?.user


    override fun saveRegisteredUser(token: String): User {
        val verificationToken: VerificationToken =
            tokenService.findByToken(token) ?: throw UserNotFoundByTokenException("{token.user.notfound}")
        val cal = Calendar.getInstance()

        if (verificationToken.expiryDate!!.time - cal.time.time <= 0) {
            throw TimeHasExpiredException("{time.expired}")
        }
        return tokenService.getUserByToken(token).let {
            it!!.enabled = true
            usersRepository.save(it)
        }
    }

    override fun getCurrentUser(): User {
        val username = detailsService.getCurrentUsername() ?: throw NoSuchUserException("{user.no.such}")
        return findByUserName(username)
    }

    override fun checkEmailExistence(email: String): Boolean {
        return usersRepository.findByEmail(email) != null
    }

    override fun checkUsernameExistence(username: String): Boolean {
        return usersRepository.findByUsername(username) != null
    }

    fun getUserById(id: Long): User =
        usersRepository.findById(id).orElseThrow {
            throw EntityNotFoundException("{entity.not.found}")
        }

    override fun blockUser(userId: Long) {
        val user: User = getUserById(userId)
        user.enabled = false
        usersRepository.save(user)
    }

    override fun unblockUser(userId: Long) {
        val user: User = getUserById(userId)
        user.enabled = true
        usersRepository.save(user)
    }

    override fun deleteUser(userId: Long) {
        tokenService.deleteTokenByUser(getUserById(userId))
        usersRepository.deleteById(userId)
    }

    override fun checkUniqueNewName(newName: String, id: Long): Boolean {
        val username = getUserById(id).username
        return  newName == username || !checkUsernameExistence(newName)
    }

    override fun checkUniqueNewEmail(newEmail: String, id: Long): Boolean {
        val email = getUserById(id).email
        return  newEmail == email || !checkEmailExistence(newEmail)
    }

    override fun changePersonalData(id: Long, user: User) {
        getUserById(id).let {
            it.username = user.username
            it.description = user.description
            it.email = user.email
            usersRepository.save(it)
        }
    }

    override fun changePassword(changePasswordDto: ChangePasswordDto) {
        val user: User = getUserById(changePasswordDto.id)

        if (passwordEncoder.matches(changePasswordDto.oldPassword, user.password)) {
            user.password = passwordEncoder.encode(changePasswordDto.newPassword)
            usersRepository.save(user)
        } else {
            throw PasswordDoesNotMatchesException("{}")
        }
    }
}
