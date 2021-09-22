package com.example.probation.controller

import com.example.probation.core.dto.ChangeInfoDto
import com.example.probation.core.dto.ChangePasswordDto
import com.example.probation.repository.UsersRepository
import com.example.probation.service.impl.UsersServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext
import java.util.Collections
import java.util.stream.Collectors

class UsersControllerTest(
    @Autowired
    private var repository: UsersRepository,
    @Autowired
    private val mapper: ObjectMapper,
    context: WebApplicationContext,
) : ControllerTest(context) {
    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder(12)

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun getCurrentUser() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/current"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Admin"))
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
    }

    @Test
    @Throws(Exception::class)
    fun getUsersForComparison() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/random"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].id").hasJsonPath())
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].username").hasJsonPath())
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].description").hasJsonPath())
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].password").doesNotHaveJsonPath())
            .andExpect(MockMvcResultMatchers.jsonPath("$[*].rating").hasJsonPath())
    }

    @Test
    @Throws(Exception::class)
    fun getTopByRating() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("/users/top"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray).andReturn()
        val json = result.response.contentAsString
        val ratings = JsonPath.parse(json).read("$.*.rating", MutableList::class.java) as List<*>
        assertEquals(ratings.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()), ratings)
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun changeRating() {
        val beforeWinnerRating = repository.findById(1L).get().rating
        val beforeLoserRating = repository.findById(2L).get().rating
        mockMvc.perform(MockMvcRequestBuilders.post("/users/{winnerId}/win/{loserId}", 1, 2)
                .header("Content-Type", "application/json")
        ).andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
        val afterWinnerRating = repository.findById(1L).get().rating
        val afterLoserRating = repository.findById(2L).get().rating
        val addition = UsersServiceImpl.ADDITION
        assertEquals(beforeWinnerRating + addition, afterWinnerRating)
        assertEquals(beforeLoserRating - addition, afterLoserRating)
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun changePassword() {
        ChangePasswordDto(1, "@Developer1609", "@Developer1600").apply {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/password")
                    .header("Content-Type", "application/json")
                    .content(mapper.writeValueAsString(this))
            )
            repository.getUserById(1)?.apply {
                assertTrue(passwordEncoder.matches(newPassword, password))
                assertFalse(passwordEncoder.matches(oldPassword, password))
            }
        }
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun tryingChangeWrongPassword() {
        ChangePasswordDto(1, "@WrongOldPassword", "@Developer1600").let {
            mockMvc.perform(MockMvcRequestBuilders.post("/users/password")
                    .header("Content-Type", "application/json")
                    .content(mapper.writeValueAsString(it))
            ).andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.FORBIDDEN.value()))
            repository.getUserById(1)?.apply {
                assertTrue(passwordEncoder.matches(it.oldPassword, password))
            }
        }
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun changeUsersData() {
        ChangeInfoDto(1, "Asdfghj", "Description", "asdfghj@gmail.com").let {
            mockMvc.perform(MockMvcRequestBuilders.put("/users/update")
                    .header("Content-Type", "application/json")
                    .content(mapper.writeValueAsString(it))
            )
            repository.getUserById(1)?.apply {
                assertEquals(username, it.username)
                assertEquals(description, it.description)
                assertEquals(email, it.email)
            }
        }
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun tryingChangeUsersDataWithNonUniqueFields() {
        ChangeInfoDto(1, "User", "Description", "asdfghj@gmail.com").let {
            mockMvc.perform(MockMvcRequestBuilders.put("/users/update")
                    .header("Content-Type", "application/json")
                    .content(mapper.writeValueAsString(it))
            ).andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.BAD_REQUEST.value()))
        }
    }
}
