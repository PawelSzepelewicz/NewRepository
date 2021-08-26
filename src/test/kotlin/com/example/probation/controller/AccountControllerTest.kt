package com.example.probation.controller

import com.example.probation.ProbationApplication
import com.example.probation.core.dto.CreateUserDto
import com.example.probation.core.dto.RoleDto
import com.example.probation.core.entity.User
import com.example.probation.repository.TokenRepository
import com.example.probation.repository.UsersRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@Sql("/init.sql")
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ProbationApplication::class]
)
class AccountControllerTest(
    @Autowired
    private val repository: UsersRepository,
    @Autowired
    private val tokenRepository: TokenRepository,
    @Autowired
    private val mapper: ObjectMapper,
) {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @Throws(Exception::class)
    fun registerUser() {
        val createdUser = CreateUserDto(
            "Person", "Information",
            "@Person0000", "person@gmail.com"
        )
        val userJson: String = mapper.writeValueAsString(createdUser)
        val role = RoleDto("USER")
        val roles: MutableSet<RoleDto> = HashSet()
        roles.add(role)
        mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:8080/accounts")
                .content(userJson)
                .header("Content-Type", "application/json")
        )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Person"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Information"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("person@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].roleName").value("USER"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotHaveJsonPath())
        val person: User? = repository.findByUsername("Person")
          assertTrue(person != null)
          assertTrue(tokenRepository.getByUser(person!!) != null)
          assertFalse(person!!.enabled)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun confirmRegistration() {
        val afterPerson = repository.findByUsername("Admin")
        assertNotNull(afterPerson)
        val verificationToken = tokenRepository.getByUser(afterPerson!!)
        assertNotNull(verificationToken)
        val token: String = verificationToken!!.token!!
        mockMvc.perform(
            MockMvcRequestBuilders.get("http://localhost:8080/accounts")
                .param("token", token)
                .header("Content-Type", "application/json")
        )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
        val beforePerson = repository.findByUsername("Admin")
        assertNotNull(beforePerson)
        assertTrue(beforePerson!!.enabled)
    }
}
