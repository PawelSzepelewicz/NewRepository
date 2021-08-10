package com.example.probation.controller;

import com.example.probation.core.entity.User;
import com.example.probation.repository.UsersRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Sql({"/user-controller.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UsersRepository repository;
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getCurrentUser() throws Exception {
        User principal = repository.findByUsername("Admin").get();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password",
                principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        mockMvc.perform(get("http://localhost:8080/users/current")
                .with(authentication(auth)))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.username").value("Admin"))
                .andExpect(authenticated())
                .andExpect(authenticated().withAuthorities(principal.getAuthorities()));
    }

    @Test
    void getUsersForComparison() throws Exception {
        mockMvc.perform(get("http://localhost:8080/users/random"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$[*].id").hasJsonPath())
                .andExpect(jsonPath("$[*].username").hasJsonPath())
                .andExpect(jsonPath("$[*].description").hasJsonPath())
                .andExpect(jsonPath("$[*].password").doesNotHaveJsonPath())
                .andExpect(jsonPath("$[*].rating").hasJsonPath()).andReturn();
    }

    @Test
    void getTopByRating() throws Exception {
        MvcResult result = mockMvc.perform(get("http://localhost:8080/users/top"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.*").isArray()).andReturn();
        String json = result.getResponse().getContentAsString();
        Integer args = JsonPath.parse(json).read("$.length()");

        for (int a = 0; a < args - 1; a++) {
            int b = a + 1;
            Integer userA = JsonPath.parse(json).read("$[" + a + "].rating");
            Integer userB = JsonPath.parse(json).read("$[" + b + "].rating");
            assertTrue(userA > userB);
        }
    }

    @Test
    void changeRating() throws Exception {
        User principal = repository.findByUsername("Admin").get();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, "password",
                principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        Integer beforeWinnerRating = repository.findById(1L).get().getRating();
        Integer beforeLoserRating = repository.findById(2L).get().getRating();
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/users/{winnerId}/win/{loserId}", 1, 2)
                .header("Content-Type", "application/json")
                .with(authentication(auth)))
                .andExpect(status().is(HttpStatus.OK.value()));
        Integer afterWinnerRating = repository.findById(1L).get().getRating();
        Integer afterLoserRating = repository.findById(2L).get().getRating();
        assertTrue(beforeWinnerRating + 15 == afterWinnerRating && beforeLoserRating - 15 == afterLoserRating);
    }
}
