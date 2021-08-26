package com.example.probation.repository

import com.example.probation.ProbationApplication
import com.example.probation.repository.RoleRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@Sql("/init.sql")
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [ProbationApplication::class]
)
class RoleRepositoryTest(
    @Autowired
    private val repository: RoleRepository
    ) {

    @Test
    fun getRoleByRoleName() {
        val role = "USER"
        val userRole = repository.getRoleByRoleName(role)
        Assertions.assertNotNull(userRole)
        Assertions.assertEquals(userRole!!.roleName, role)
    }
}
