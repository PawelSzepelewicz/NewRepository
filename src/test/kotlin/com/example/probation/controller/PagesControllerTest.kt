package com.example.probation.controller

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.context.WebApplicationContext

class PagesControllerTest(context: WebApplicationContext) : ControllerTest(context) {
    @Test
    @Throws(Exception::class)
    fun login() {
        mockMvc.perform(SecurityMockMvcRequestBuilders
                .formLogin("/login")
                .user("Admin")
                .password("@Admin0000")
        ).andExpect(MockMvcResultMatchers.status().`is`(HttpStatus.FOUND.value()))
            .andExpect(SecurityMockMvcResultMatchers.authenticated())
    }
}
