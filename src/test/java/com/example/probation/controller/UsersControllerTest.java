package com.example.probation.controller;

import com.example.probation.core.entity.Role;
import com.example.probation.repository.UsersRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Sql({"/init.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsersRepository repository;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserDetailsService detailsService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithUserDetails("Admin")
    void getCurrentUser() throws Exception {
        final var roles = Set.of(new Role("ADMIN"), new Role("USER"));
        mockMvc.perform(get("http://localhost:8080/users/current"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.username").value("Admin"))
                .andExpect(authenticated())
                .andExpect(authenticated().withAuthorities(roles));
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
                .andExpect(jsonPath("$[*].rating").hasJsonPath());
    }

    @Test
    void getTopByRating() throws Exception {
        final MvcResult result = mockMvc.perform(get("http://localhost:8080/users/top"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.*").isArray()).andReturn();
        final String json = result.getResponse().getContentAsString();
        final List<Integer> ratings = (List<Integer>) JsonPath.parse(json).read("$.*.rating", List.class);
        assertEquals(ratings.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()), ratings);
    }

    @Test
    @WithUserDetails("Admin")
    void changeRating() throws Exception {
        final Integer beforeWinnerRating = repository.findById(1L).get().getRating();
        final Integer beforeLoserRating = repository.findById(2L).get().getRating();
        mockMvc.perform(post("http://localhost:8080/users/{winnerId}/win/{loserId}", 1, 2)
                .header("Content-Type", "application/json"))
                .andExpect(status().is(HttpStatus.OK.value()));
        final Integer afterWinnerRating = repository.findById(1L).get().getRating();
        final Integer afterLoserRating = repository.findById(2L).get().getRating();
        final Integer addition = 15;
        assertEquals(beforeWinnerRating + addition, (int) afterWinnerRating);
        assertEquals(beforeLoserRating - addition, (int) afterLoserRating);
    }
}
