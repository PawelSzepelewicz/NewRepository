package com.example.probation.repository

import com.example.probation.core.enums.Roles
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired


class RoleRepositoryTest(
    @Autowired
    private val repository: RoleRepository
) : RepositoryTest() {

    @Test
    fun getRoleByRoleName() {
        val role = Roles.USER.role
        repository.getRoleByRoleName(role)?.apply {
            Assertions.assertNotNull(this)
            Assertions.assertEquals(roleName, role)
        }
    }
}
