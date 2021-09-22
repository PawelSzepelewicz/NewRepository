package com.example.probation.service

import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.core.entity.Role
import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.core.enums.Actions
import com.example.probation.core.enums.Roles
import com.example.probation.exception.EntityNotFoundException
import com.example.probation.exception.PasswordDoesNotMatchesException
import com.example.probation.repository.UsersRepository
import com.example.probation.service.impl.CustomUserDetailsService
import com.example.probation.service.impl.UsersServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UsersServiceImplTest {
    @Mock
    lateinit var usersRepository: UsersRepository
    @Mock
    lateinit var roleService: RoleService
    @Mock
    lateinit var tokenService: TokenService
    @Mock
    lateinit var detailsService: CustomUserDetailsService
    @Mock
    lateinit var eventPublisher: ApplicationEventPublisher
    @Mock
    lateinit var kafkaService: KafkaProducerService
    @InjectMocks
    lateinit var service: UsersServiceImpl

    @Spy
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(12)

    @Test
    fun calculateWinnerRating() {
        val winnerRating = service.calculateWinnerRating(1000)
        assertEquals(winnerRating, 1015)
    }

    @Test
    fun calculateLoserRating() {
        val loserRating = service.calculateLoserRating(1000)
        assertEquals(loserRating, 985)
    }

    @Test
    fun registerNewUser() {
        val defaultPassword = "@Password"
        val testUser = User("Pawel")
        testUser.password = defaultPassword
        val testRole = Role()
        testRole.id = 1L
        testRole.roleName = Roles.USER.role
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn("Admin")
        Mockito.`when`<Any>(usersRepository.save(any())).thenAnswer {  it.arguments[0] }
        Mockito.`when`(roleService.getRoleByRoleName(any(String::class.java) ?: String()))
            .thenAnswer { testRole }
        service.registerNewUser(testUser).apply {
            assertTrue(passwordEncoder.matches(defaultPassword, password))
            assertTrue(roles.contains(testRole))
        }
        Mockito.verify(kafkaService).send(Actions.UNBLOCK.action, "Admin", testUser.username)
    }

    @Test
    fun getTopUsersByRating() {
        val userA = User(rating = 3000)
        val userB = User(rating = 2000)
        mutableListOf(userA, userB).apply {
            Mockito.`when`(usersRepository.findAllByOrderByRatingDesc()).thenAnswer { this }
        }
        service.getTopUsersByRating().apply {
            assertTrue(get(0).rating >= get(1).rating)
        }
    }

    @Test
    fun saveRegisteredUser() {
        val user = User(enabled = false)
        val token = "902a8cf0-63a7-444f-a424-8960907e7een"
        VerificationToken(token, user, LocalDateTime.now().plusMinutes(120)).apply {
            Mockito.`when`(tokenService.findByToken(token)).thenReturn(this)
            Mockito.`when`(tokenService.getUserByToken(token)).thenReturn(user)
            Mockito.`when`<Any>(usersRepository.save(any())).thenAnswer { i -> i.arguments[0] }
        }
        service.saveRegisteredUser(token)?.apply { assertTrue(enabled) }
    }

    @Test
    fun getUserByToken() {
        val token = "902a8cf0-63a7-444f-a424-8960907e7een"
        val wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een"
        VerificationToken(token, User(), LocalDateTime.now().plusMinutes(120)).apply {
            Mockito.`when`(tokenService.findByToken(token)).thenReturn(this)
            assertNotNull(service.getUserByToken(token))
            assertNull(service.getUserByToken(wrongToken))
            assertSame(service.getUserByToken(token), user)
        }
    }

    @Test
    fun getCurrentUser() {
        val user = User("Current", enabled = true)
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn("Current")
        Mockito.`when`(usersRepository.findByUsername("Current")).thenReturn(user)
        assertEquals(service.getCurrentUser(), user)
        assertTrue(service.getCurrentUser().enabled)
        val wrongName = "Wrong"
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn(wrongName)
        assertThrows(
            EntityNotFoundException::class.java
        ) { service.getCurrentUser() }
    }

    @Test
    fun findByUsername() {
        User("User").also {
            Mockito.`when`(usersRepository.findByUsername("User")).thenReturn(it)
        }
        val person: User = service.findByUserName("User")
        assertNotNull(person)
    }

    @Test
    fun getUsersForComparison() {
        mutableListOf(User( "UserA"), User("UserB")).apply {
            Mockito.`when`(usersRepository.getRandomUsers()).thenReturn(this)
        }
        service.getUsersForComparison().apply {
            assertNotEquals(get(0).username, get(1).username)
            assertEquals(2, size)
        }
    }

    @Test
    fun redefineRating() {
        val loser = User(rating = 3000)
        val winner = User("Pawel", rating = 2500)
        arrayListOf(winner, loser).apply {
            Mockito.`when`(usersRepository.saveAll(this)).thenReturn(this)
        }
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn("Admin")
        service.redefineRating(winner, loser)
        assertTrue(winner.rating == 2515 && loser.rating == 2985)
        Mockito.verify(kafkaService).send(Actions.UNBLOCK.action, "Admin", winner.username)
    }

    @Test
    fun checkEmailExistence() {
        val user = User(email ="email@gmail.com")
        val email = "email@gmail.com"
        val nonexistentEmail = "nonEx@gmail.com"
        Mockito.`when`(usersRepository.findByEmail(email)).thenReturn(user)
        assertTrue(service.checkEmailExistence(email))
        assertFalse(service.checkEmailExistence(nonexistentEmail))
    }

    @Test
    fun checkUsernameExistence() {
        val user = User("UserByName")
        val name = "UserByName"
        val nonexistentName = "nonEx"
        Mockito.`when`(usersRepository.findByUsername(name)).thenReturn(user)
        assertTrue(service.checkUsernameExistence(name))
        assertFalse(service.checkUsernameExistence(nonexistentName))
    }

    @Test
    fun changePassword() {
        val newPassword = "@NewPassword"
        val password = "@Password"
        val wrongPassword = "@WrongPassword"
        val user = User(password = passwordEncoder.encode(password))
        val passwordDto = ChangePasswordDto(1, password, newPassword)
        val wrongDto = ChangePasswordDto(1, wrongPassword, newPassword)
        Mockito.`when`(usersRepository.getUserById(1)).thenReturn(user)
        Mockito.`when`<Any>(service.getUserById(1)).thenAnswer { user }
        Mockito.`when`(usersRepository.save(any())).thenReturn(user)
        service.changePassword(passwordDto)
        assertTrue(passwordEncoder.matches(newPassword, user.password))
        assertFalse(passwordEncoder.matches(password, user.password))
        assertThrows(PasswordDoesNotMatchesException::class.java) { service.changePassword(wrongDto) }
        user.username?.let { Mockito.verify(kafkaService).send(Actions.UNBLOCK.action, it) }
    }

    @Test
    fun changePersonalDate() {
        val user = User("Name", "Description", "email@gmail.com")
        val changedUser = User("NewName", "NewDescription", "new@gmail.com")
        changedUser.id = 1
        Mockito.`when`(usersRepository.getUserById(1)).thenReturn(user)
        Mockito.`when`<Any>(service.getUserById(1)).thenAnswer { user }
        Mockito.`when`(usersRepository.save(any())).thenReturn(user)
        service.changePersonalData(changedUser)
        assertEquals(changedUser.username, user.username)
        assertEquals(user.description, changedUser.description)
        assertEquals(user.email, changedUser.email)
    }

    @Test
    fun blockUser() {
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn("Admin")
        User("Pawel", enabled = true).apply {
            Mockito.`when`(usersRepository.getUserById(1)).thenReturn(this)
            Mockito.`when`<Any>(service.getUserById(1)).thenAnswer { this }
            Mockito.`when`(usersRepository.save(any())).thenReturn(this)
        }.also {
            service.blockUser(1)
        }.let { assertFalse(it.enabled) }
        Mockito.verify(kafkaService).send(Actions.BLOCK.action, "Admin", "Pawel")
    }

    @Test
    fun unblockUser() {
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn("Admin")
        User("Pawel", enabled = false).apply {
            Mockito.`when`(usersRepository.getUserById(1)).thenReturn(this)
            Mockito.`when`<Any>(service.getUserById(1)).thenAnswer { this }
            Mockito.`when`(usersRepository.save(any())).thenReturn(this)
        }.also {
            service.unblockUser(1)
        }.let { assertTrue(it.enabled) }
        Mockito.verify(kafkaService).send(Actions.UNBLOCK.action, "Admin", "Pawel")
    }
}
