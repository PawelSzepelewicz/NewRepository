package com.example.probation.service

import com.example.probation.core.entity.Role
import com.example.probation.core.entity.User
import com.example.probation.core.entity.VerificationToken
import com.example.probation.exception.ForbiddenException
import com.example.probation.exception.NoSuchUserException
import com.example.probation.exception.UserNotFoundByTokenException
import com.example.probation.repository.UsersRepository
import com.example.probation.service.impl.CustomUserDetailsService
import com.example.probation.service.impl.UsersServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

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
    @InjectMocks
    lateinit var service: UsersServiceImpl
    @Spy
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(12)

    private fun getUsersRoles(): Set<Role> {
        return mutableSetOf(Role("USER"))
    }

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
        val defaultPassword = "Pasha2000"
        val testUser = User()
        testUser.username = "Pawel"
        testUser.description = "Description"
        testUser.email = "email@outlook.com"
        testUser.password = defaultPassword
        val testRole = Role()
        testRole.id = 1L
        testRole.roleName = "USER"
        Mockito.`when`<Any>(usersRepository.save(any())).thenAnswer { i: InvocationOnMock ->
            i.arguments[0]
        }
        Mockito.`when`(roleService.getRoleByRoleName(any(String::class.java) ?: String()))
            .thenAnswer { testRole }
        val (_, _, _, _, password, roles) = service.registerNewUser(testUser)
        assertTrue(passwordEncoder.matches(defaultPassword, password))
        assertTrue(roles.contains(testRole))
    }

    @Test
    fun getTopUsersByRating() {
        val userA = User(
            "Higher", "Description", "higher@gmail.com",
            3000, "1@higher", getUsersRoles(), true
        )
        val userB = User(
            "Lower", "Description", "lower@gmail.com",
            2000, "2@lower", getUsersRoles(), true
        )
        val top: MutableList<User> = ArrayList()
        top.add(userA)
        top.add(userB)
        Mockito.`when`(usersRepository.findAllByOrderByRatingDesc()).thenAnswer { top }
        val usersTop = service.getTopUsersByRating()
        assertTrue(usersTop[0].rating >= usersTop[1].rating)
    }

    @Test
    fun saveRegisteredUser() {
        val user = User(
            "Higher", "Description", "higher@gmail.com",
            3000, "1@higher", getUsersRoles(), false
        )
        val token = "902a8cf0-63a7-444f-a424-8960907e7een"
        val wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een"
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, 2)
        val verificationToken = VerificationToken(token, user, calendar.time)
        Mockito.`when`(tokenService.findByToken(token)).thenReturn(verificationToken)
        Mockito.`when`(tokenService.getUserByToken(token)).thenReturn(user)
        Mockito.`when`<Any>(usersRepository.save(any())).thenAnswer { i: InvocationOnMock ->
            i.arguments[0]
        }
        val registeredUser = service.saveRegisteredUser(token)
        assertTrue(registeredUser.enabled)
        Assertions.assertThrows(UserNotFoundByTokenException::class.java) {
            service.saveRegisteredUser(
                wrongToken
            )
        }
    }

    @Test
    fun getUserByToken() {
        val token = "902a8cf0-63a7-444f-a424-8960907e7een"
        val wrongToken = "802a8cf0-63a7-444f-a424-8960907e7een"
        val user = User(
            "Simple", "Description", "simple@gmail.com",
            3000, "1@higher", getUsersRoles(), false
        )
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR_OF_DAY, +2)
        val verificationToken = VerificationToken(token, user, calendar.time)
        Mockito.`when`(tokenService.findByToken(token)).thenReturn(verificationToken)
        assertNotNull(service.getUserByToken(token))
        assertSame(service.getUserByToken(token), user)
    }

    @Test
    fun getCurrentUser() {
        val user = User(
            "Current", "Description",
            "current@gmail.com", 3000, "1@current", getUsersRoles(), true
        )
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn("Current")
        Mockito.`when`(usersRepository.findByUsername("Current")).thenReturn(user)
        assertEquals(service.getCurrentUser(), user)
        assertTrue(service.getCurrentUser().enabled)
        assertEquals(service.getCurrentUser().roles, getUsersRoles())
        val wrongName = "Wrong"
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn(wrongName)
        assertThrows(
            ForbiddenException::class.java
        ) { service.getCurrentUser() }
        val nullName: String? = null
        Mockito.`when`(detailsService.getCurrentUsername()).thenReturn(nullName)
        assertThrows(
            NoSuchUserException::class.java
        ) { service.getCurrentUser() }
    }

    @Test
    fun findByUsername() {
        val user = User(
            "User", "Description",
            "user@gmail.com", 3000, "1@cuser", getUsersRoles(), true
        )
        Mockito.`when`(usersRepository.findByUsername(any())).thenReturn(user)
        val person: User = service.findByUserName("User")
        assertNotNull(person)
    }

    @Test
    fun getUsersForComparison() {
        val userA = User(
            "UserA", "1234",
            "a@gmail.com", 3000, "1@cuser", getUsersRoles(), true
        )
        val userB = User(
            "UserB", "1234",
            "a@gmail.com", 2500, "2@cuser", getUsersRoles(), true
        )
        val random: MutableList<User> = ArrayList()
        random.add(userA)
        random.add(userB)
        Mockito.`when`(usersRepository.getRandomUsers()).thenReturn(random)
        val userList = service.getUsersForComparison()
        assertNotEquals(userList[0].username, userList[1].username)
        assertEquals(2, userList.size)
    }

    @Test
    fun redefineRating() {
        val loser = User(
            "UserA", "1234", "a@gmail.com", 3000,
            "1@cuser", getUsersRoles(), true
        )
        val winner = User(
            "UserB", "1234", "a@gmail.com", 2500,
            "2@cuser", getUsersRoles(), true
        )
        val usersList = arrayListOf(winner, loser)
        Mockito.`when`(usersRepository.saveAll(usersList)).thenReturn(usersList)
        service.redefineRating(winner, loser)
        assertTrue(winner.rating == 2515 && loser.rating == 2985)
    }

    @Test
    fun checkEmailExistence() {
        val user = User(
            "UserByEmail", "1234", "email@gmail.com",
            3000, "1@cuser", getUsersRoles(), true
        )
        val email = "email@gmail.com"
        val nonexistentEmail = "nonEx@gmail.com"
        Mockito.`when`(usersRepository.findByEmail(email)).thenReturn(user)
        assertTrue(service.checkEmailExistence(email))
        assertFalse(service.checkEmailExistence(nonexistentEmail))
    }

    @Test
    fun checkUsernameExistence() {
        val user = User(
            "UserByName", "1234", "email@gmail.com",
            3000, "1@cuser", getUsersRoles(), true
        )
        val name = "UserByName"
        val nonexistentName = "nonEx"
        Mockito.`when`(usersRepository.findByUsername(name)).thenReturn(user)
        assertTrue(service.checkUsernameExistence(name))
        assertFalse(service.checkUsernameExistence(nonexistentName))
    }
}
