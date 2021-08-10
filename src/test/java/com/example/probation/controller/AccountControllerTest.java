package com.example.probation.controller;

import com.example.probation.core.dto.CreateUserDto;
import com.example.probation.core.dto.RoleDto;
import com.example.probation.repository.TokenRepository;
import com.example.probation.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Sql({"/roles.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UsersRepository repository;
    @Autowired
    TokenRepository tokenRepository;

    @Test
    void registerUser() throws Exception {
        CreateUserDto createdUser = new CreateUserDto("Person", "Information",
                "@Person0000", "person@gmail.com");
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(createdUser);
        RoleDto role = new RoleDto("USER");
        Set<RoleDto> roles = new HashSet<>();
        roles.add(role);
        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/accounts")
                .content(userJson)
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Person"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Information"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("person@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0].role").value("USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotHaveJsonPath());
        assertTrue(repository.findByUsername("Person").isPresent());
        assertTrue(tokenRepository.getByUser(repository.findByUsername("Person").get()).isPresent());
        assertFalse(repository.findByUsername("Person").get().isEnabled());
    }

    @Test
    void confirmRegistration() throws Exception {
        String token = tokenRepository.getByUser(repository.findByUsername("Admin").get()).get().getToken();
        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/accounts")
                .param("token", token)
                .header("Content-Type", "application/json"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
        assertTrue(repository.findByUsername("Admin").get().isEnabled());
    }
}
