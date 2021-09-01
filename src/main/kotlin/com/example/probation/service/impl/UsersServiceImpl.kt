package com.example.probation.service.impl

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.entity.User
import com.example.probation.core.enums.Actions
import com.example.probation.core.enums.Current
import com.example.probation.core.enums.Roles
import com.example.probation.event.OnRegistrationCompleteEvent
import com.example.probation.exception.EntityNotFoundException
import com.example.probation.exception.ForbiddenException
import com.example.probation.exception.PasswordDoesNotMatchesException
import com.example.probation.exception.TimeHasExpiredException
import com.example.probation.repository.UsersRepository
import com.example.probation.service.KafkaProducerService
import com.example.probation.service.RoleService
import com.example.probation.service.TokenService
import com.example.probation.service.UsersService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UsersServiceImpl(
    private val usersRepository: UsersRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleService: RoleService,
    private val tokenService: TokenService,
    private val detailsService: CustomUserDetailsService,
    private val eventPublisher: ApplicationEventPublisher,
    private val kafkaProducerService: KafkaProducerService
) : UsersService {
    companion object {
        const val ADDITION = 15
    }

    override fun registerNewUser(newUser: User): User = newUser.apply {
        password = passwordEncoder.encode(newUser.password)
        mutableSetOf(roleService.getRoleByRoleName(Roles.USER.role))
    }.let { usersRepository.save(it) }
        .also { eventPublisher.publishEvent(OnRegistrationCompleteEvent(it)) }

    override fun redefineRating(winner: User, loser: User) {
        val players: MutableList<User> = ArrayList()
        loser.rating = calculateLoserRating(loser.rating)
        winner.rating = calculateWinnerRating(winner.rating)
        players.add(winner)
        players.add(loser)
        usersRepository.saveAll(players)
        sendLog(Actions.CHOOSE.action, Current.CURRENT.value, winner.username)
    }

    override fun getUsersForComparison() =
        usersRepository.getRandomUsers()

    override fun getTopUsersByRating() =
        usersRepository.findAllByOrderByRatingDesc();

    override fun calculateWinnerRating(currentRating: Int) = currentRating + ADDITION

    override fun calculateLoserRating(currentRating: Int) = currentRating - ADDITION

    override fun findByUserName(username: String) =
        usersRepository.findByUsername(username) ?: throw EntityNotFoundException("{entity.notfound}")

    override fun getUserByToken(token: String) =
        tokenService.findByToken(token)?.user

    override fun saveRegisteredUser(token: String) =
        tokenService.findByToken(token)?.let { verificationToken ->
            if (verificationToken.expiryDate?.isAfter(LocalDateTime.now()) != false) {
                throw TimeHasExpiredException("{time.expired}")
            }
            tokenService.getUserByToken(token)?.apply {
                sendLog(Actions.CONFIRM.action, username, null)
                enabled = true
                usersRepository.save(this)
            }
        }

    override fun getCurrentUser() =
        findByUserName(detailsService.getCurrentUsername())

    override fun checkEmailExistence(email: String) =
        usersRepository.findByEmail(email) != null

    override fun checkUsernameExistence(username: String) =
        usersRepository.findByUsername(username) != null

    private fun getUserById(id: Long): User =
        usersRepository.findById(id).orElseThrow {
            throw EntityNotFoundException("{entity.not.found}")
        }

    override fun blockUser(userId: Long) {
        getUserById(userId).apply {
            enabled = false
            usersRepository.save(this)
        }.also { sendLog(Actions.BLOCK.action, Current.CURRENT.value, it.username) }
    }

    override fun unblockUser(userId: Long) {
        getUserById(userId).apply {
            enabled = true
            usersRepository.save(this)
        }.also { sendLog(Actions.UNBLOCK.action, Current.CURRENT.value, it.username) }
    }

    override fun deleteUser(userId: Long) {
        getUserById(userId).let {
            tokenService.deleteTokenByUser(it)
            usersRepository.deleteById(userId)
            sendLog(Actions.DELETE.action, Current.CURRENT.value, it.username)
            sendLog(Actions.DELETED.action, it.username, Current.CURRENT.value)
        }
    }

    override fun checkUniqueNewName(newName: String, id: Long) =
        getUserById(id).username.let {
            newName == it || !checkUsernameExistence(newName)
        }

    override fun checkUniqueNewEmail(newEmail: String, id: Long) =
        getUserById(id).email.let {
            newEmail == it || !checkEmailExistence(newEmail)
        }

    override fun changePersonalData(user: User) {
        user.id?.let { userId ->
            getUserById(userId).let {
                it.id = user.id
                it.username = user.username
                it.description = user.description
                it.email = user.email
                usersRepository.save(it)
            }
        }
        sendLog(Actions.EDIT.action, Current.CURRENT.value, null)
    }

    override fun changePassword(changePasswordDto: ChangePasswordDto) {
        val user: User = getUserById(changePasswordDto.id)

        if (passwordEncoder.matches(changePasswordDto.oldPassword, user.password)) {
            user.password = passwordEncoder.encode(changePasswordDto.newPassword)
            usersRepository.save(user)
        } else {
            throw PasswordDoesNotMatchesException("{password.not.matches}")
        }

        sendLog(Actions.CHANGE_PASSWORD.action, Current.CURRENT.value, null)
    }

    private fun searchCurrentUsernameIfExist(name: String?) =
        if (name == Current.CURRENT.value) {
            try {
                detailsService.getCurrentUsername()
            } catch (e: ForbiddenException) {
                null
            }
        } else {
            name
        }

    override fun sendLog(action: String, name: String?, subject: String?) {
        val subjectName = searchCurrentUsernameIfExist(subject)
        val username = searchCurrentUsernameIfExist(name)

        if (username != null) {
            kafkaProducerService.send(action, username, subjectName)
        }
    }
}
