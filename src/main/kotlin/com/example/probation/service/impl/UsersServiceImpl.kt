package com.example.probation.service.impl

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.entity.User
import com.example.probation.core.enums.Actions
import com.example.probation.core.enums.Roles
import com.example.probation.event.OnRegistrationCompleteEvent
import com.example.probation.exception.EntityNotFoundException
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
    private val kafkaService: KafkaProducerService
) : UsersService {

    override fun registerNewUser(newUser: User) = newUser.apply {
        password = passwordEncoder.encode(newUser.password)
        roleService.getRoleByRoleName(Roles.USER.role)?.let { role ->
            roles = mutableSetOf(role)
        }
    }.let { usersRepository.save(it) }
        .also {
            eventPublisher.publishEvent(OnRegistrationCompleteEvent(it))
            val currentUsername = detailsService.getCurrentUsername()
            kafkaService.send(Actions.REGISTER.action, currentUsername, it.username)
        }

    override fun redefineRating(winner: User, loser: User) {
        val players: MutableList<User> = ArrayList()
        loser.rating = calculateLoserRating(loser.rating)
        winner.rating = calculateWinnerRating(winner.rating)
        players.add(winner)
        players.add(loser)
        usersRepository.saveAll(players)
        kafkaService.send(Actions.CHOOSE.action, detailsService.getCurrentUsername(), winner.username)
    }

    override fun getUsersForComparison() = usersRepository.getRandomUsers()

    override fun getTopUsersByRating() = usersRepository.findAllByOrderByRatingDesc()

    override fun calculateWinnerRating(currentRating: Int) = currentRating + ADDITION

    override fun calculateLoserRating(currentRating: Int) = currentRating - ADDITION

    override fun findByUserName(username: String) =
        usersRepository.findByUsername(username) ?: throw EntityNotFoundException("{entity.notfound}")

    override fun getUserByToken(token: String) =
        tokenService.findByToken(token)?.user

    override fun saveRegisteredUser(token: String) =
        tokenService.findByToken(token)?.let { verificationToken ->
            if (verificationToken.expiryDate?.isAfter(LocalDateTime.now()) != true) {
                throw TimeHasExpiredException("{time.expired}")
            }
            tokenService.getUserByToken(token)?.enable(true)?.let {
                usersRepository.save(it)
            }.also {
                it?.username?.let { name -> kafkaService.send(Actions.CONFIRM.action, name) }
            }
        }

    private fun User.enable(isEnabled: Boolean) = apply { enabled = isEnabled }

    override fun getCurrentUser() =
        findByUserName(detailsService.getCurrentUsername())

    override fun checkEmailExistence(email: String) =
        usersRepository.findByEmail(email) != null

    override fun checkUsernameExistence(username: String) =
        usersRepository.findByUsername(username) != null

    override fun getUserById(id: Long): User =
        usersRepository.getUserById(id) ?: throw EntityNotFoundException("{entity.not.found}")


    override fun blockUser(userId: Long) {
        getUserById(userId).enable(false).let {
            usersRepository.save(it)
        }.also {
            kafkaService.send(Actions.BLOCK.action, detailsService.getCurrentUsername(), it.username)
        }
    }

    override fun unblockUser(userId: Long) {
        getUserById(userId).enable(true).let {
            usersRepository.save(it)
        }.also {
            kafkaService.send(Actions.UNBLOCK.action, detailsService.getCurrentUsername(), it.username)
        }
    }

    override fun deleteUser(userId: Long) {
        getUserById(userId).let {
            tokenService.deleteTokenByUser(it)
            usersRepository.deleteById(userId)
            val currentUsername = detailsService.getCurrentUsername()
            kafkaService.send(Actions.DELETE.action, currentUsername, it.username)
            it.username?.let { name ->
                kafkaService.send(Actions.DELETED.action, name, currentUsername)
            }
        }
    }

    override fun checkUniqueNewName(newName: String, id: Long) =
        getUserById(id).run {
            newName == username || !checkUsernameExistence(newName)
        }

    override fun checkUniqueNewEmail(newEmail: String, id: Long) =
        getUserById(id).run {
            newEmail == email || !checkEmailExistence(newEmail)
        }

    override fun changePersonalData(user: User) {
        user.id?.let { userId ->
            getUserById(userId).apply {
                id = user.id
                username = user.username
                description = user.description
                email = user.email
            }.let {
                usersRepository.save(it)
            }.also {
                it.username?.let { name -> kafkaService.send(Actions.EDIT.action, name) }
            }
        }
    }

    override fun changePassword(changePasswordDto: ChangePasswordDto) {
        getUserById(changePasswordDto.id).apply {
            if (passwordEncoder.matches(changePasswordDto.oldPassword, password)) {
                password = passwordEncoder.encode(changePasswordDto.newPassword)
                usersRepository.save(this)
            } else {
                throw PasswordDoesNotMatchesException("{password.not.matches}")
            }
        }.also {
            it.username?.let { name ->
                kafkaService.send(Actions.CHANGE_PASSWORD.action, name)
            }
        }
    }

    companion object {
        const val ADDITION = 15
    }
}
