package com.example.probation.service

import com.example.probation.core.entity.Role
import com.example.probation.core.enums.Roles
import com.example.probation.repository.RoleRepository
import com.example.probation.service.impl.RoleServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class RoleServiceImplTest {
    @Mock
    lateinit var repository: RoleRepository
    @InjectMocks
    lateinit var service: RoleServiceImpl

    @Test
    fun getRoleByRoleName() {
        val userRole = Roles.USER.role
        val role = Role(userRole)
        Mockito.`when`(repository.getRoleByRoleName(any(String::class.java) ?: String())).thenReturn(role)
        Assertions.assertEquals(service.getRoleByRoleName(userRole).roleName, role.roleName)
    }
}
