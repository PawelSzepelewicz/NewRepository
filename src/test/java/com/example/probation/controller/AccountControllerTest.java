package com.example.probation.controller;

import com.example.probation.core.dto.CreateUserDto;
import com.example.probation.core.dto.RoleDto;
import com.example.probation.repository.TokenRepository;
import com.example.probation.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@Sql({"/init.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsersRepository repository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private ObjectMapper mapper;

    @Test
    void registerUser() throws Exception {
        final CreateUserDto createdUser = new CreateUserDto("Person", "Information",
                "@Person0000", "person@gmail.com");
        final String userJson = mapper.writeValueAsString(createdUser);
        final RoleDto role = new RoleDto("USER");
        final Set<RoleDto> roles = new HashSet<>();
        roles.add(role);
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/accounts")
                .content(userJson)
                .header("Content-Type", "application/json"))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.username").value("Person"))
                .andExpect(jsonPath("$.description").value("Information"))
                .andExpect(jsonPath("$.email").value("person@gmail.com"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0].role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotHaveJsonPath());
        final var person = repository.findByUsername("Person");
        assertTrue(person.isPresent());
        assertTrue(tokenRepository.getByUser(person.get()).isPresent());
        assertFalse(person.get().isEnabled());
    }

    @Test
    void confirmRegistration() throws Exception {
        final var afterPerson = repository.findByUsername("Admin");
        assertTrue(afterPerson.isPresent());
        final var verificationToken = tokenRepository.getByUser(afterPerson.get());
        assertTrue(verificationToken.isPresent());
        final String token = verificationToken.get().getToken();
        mockMvc.perform(get("http://localhost:8080/accounts")
                .param("token", token)
                .header("Content-Type", "application/json"))
                .andExpect(status().is(HttpStatus.OK.value()));
        final var beforePerson = repository.findByUsername("Admin");
        assertTrue(beforePerson.isPresent());
        assertTrue(beforePerson.get().isEnabled());
    }
}
