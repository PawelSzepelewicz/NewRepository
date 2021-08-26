package com.example.probation.controller

import com.example.probation.ProbationApplication
import com.example.probation.controller.PagesController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@Sql("/init.sql")
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ProbationApplication::class]
)
class PagesControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Test
    @Throws(Exception::class)
    fun login() {
        mockMvc.perform(
            SecurityMockMvcRequestBuilders
                .formLogin("http://localhost:8080/login")
                .user("Admin")
                .password("@Admin0000")
        )
            .andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.FOUND.value()))
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
    }
}