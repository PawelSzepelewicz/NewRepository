package com.example.probation.controller

import com.example.probation.core.dto.CreateUserDto
import com.example.probation.core.entity.User
import com.example.probation.exception.EntityNotFoundException
import com.example.probation.repository.TokenRepository
import com.example.probation.repository.UsersRepository
import com.example.probation.service.UsersService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext

class AccountControllerTest(
    @Autowired
    private val repository: UsersRepository,
    @Autowired
    private val tokenRepository: TokenRepository,
    @Autowired
    private val mapper: ObjectMapper,
    @Autowired
    private val service: UsersService,
    context: WebApplicationContext,
) : ControllerTest(context) {

    @Test
    @Throws(Exception::class)
    @WithUserDetails("Admin")
    fun registerUser() {
        val createdUser = CreateUserDto(
            "Person", "Information",
            "@Person0000", "person@gmail.com"
        )
        val userJson: String = mapper.writeValueAsString(createdUser)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
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
        assertTrue(person?.let { tokenRepository.getByUser(it) } != null)
        if (person != null) {
            assertFalse(person.enabled)
        }
    }

    @Test
    @Throws(Exception::class)
    fun confirmRegistration() {
        val afterPerson = repository.findByUsername("User")
        assertNotNull(afterPerson)
        val verificationToken = afterPerson?.let { tokenRepository.getByUser(it) }
        assertNotNull(verificationToken)
        val token: String? = verificationToken?.token
        mockMvc.perform(
            MockMvcRequestBuilders.get("/accounts")
                .param("token", token)
                .header("Content-Type", "application/json")
        )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
        val beforePerson = repository.findByUsername("User")
        assertNotNull(beforePerson)
        assertTrue(beforePerson!!.enabled)
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun blockAndUnblockUser() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts/block/{userId}", 1)
                .header("Content-Type", "application/json")
        )
        repository.getUserById(1)?.apply {
            assertFalse(enabled)
        }
        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts/unblock/{userId}", 1)
                .header("Content-Type", "application/json")
        )
        repository.getUserById(1)?.apply {
            assertTrue(enabled)
        }
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun deleteUser() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/accounts/{userId}", 1)
                .header("Content-Type", "application/json")
        )
        assertThrows<EntityNotFoundException> {
            service.getUserById(1)
        }
        assertTrue(repository.getUserById(1)?.username == null)
    }
}
