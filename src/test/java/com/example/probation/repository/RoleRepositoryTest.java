package com.example.probation.repository;

import com.example.probation.core.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@Sql({"/init.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoleRepositoryTest {
    @Autowired
    private RoleRepository repository;

    @Test
    public void getRoleByRole() {
        final String role = "USER";
        final Role userRole = repository.getRoleByRole(role);
        assertNotNull(userRole);
        assertEquals(userRole.getRole(), role);
    }
}
