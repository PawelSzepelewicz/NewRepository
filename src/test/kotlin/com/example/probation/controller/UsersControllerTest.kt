package com.example.probation.controller

import com.example.probation.ProbationApplication
import com.example.probation.core.entity.Role
import com.example.probation.repository.UsersRepository
import com.example.probation.service.impl.UsersServiceImpl
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*
import java.util.stream.Collectors

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@Sql("/init.sql")
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ProbationApplication::class]
)
class UsersControllerTest(
    @Autowired
    private var repository: UsersRepository,
    @Autowired
    private var context: WebApplicationContext) {
    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
            .build()
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(Exception::class)
    fun getCurrentUser() {
        val roles = mutableSetOf(Role("ADMIN"), Role("USER"))
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/users/current"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Admin"))
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
            .andExpect(SecurityMockMvcResultMatchers.authenticated().withAuthorities(roles))
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun getUsersForComparison() {
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/users/random"))
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
    @Throws(java.lang.Exception::class)
    fun getTopByRating() {
        val result = mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/users/top"))
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.*").isArray).andReturn()
        val json = result.response.contentAsString
        val ratings = JsonPath.parse(json).read(
            "$.*.rating",
            MutableList::class.java
        ) as List<*>
        Assertions.assertEquals(
            ratings.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()),
            ratings
        )
    }

    @Test
    @WithUserDetails("Admin")
    @Throws(java.lang.Exception::class)
    fun changeRating() {
        val beforeWinnerRating = repository.findById(1L).get().rating
        val beforeLoserRating = repository.findById(2L).get().rating
        mockMvc.perform(
            MockMvcRequestBuilders.post("http://localhost:8080/users/{winnerId}/win/{loserId}", 1, 2)
                .header("Content-Type", "application/json")
        )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.OK.value()))
        val afterWinnerRating = repository.findById(1L).get().rating
        val afterLoserRating = repository.findById(2L).get().rating
        val addition = UsersServiceImpl.ADDITION
        Assertions.assertEquals(beforeWinnerRating + addition, afterWinnerRating)
        Assertions.assertEquals(beforeLoserRating - addition, afterLoserRating)
    }
}

