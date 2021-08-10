package com.example.probation.service.impl;

import com.example.probation.core.entity.Role;
import com.example.probation.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository repository;
    @InjectMocks
    private RoleServiceImpl service;

    @Test
    void getROleByRole() {
        final Role role = new Role();
        final String newRole = "USER";
        role.setRole(newRole);
        final String userRole = "USER";
        Mockito.when(repository.getRoleByRole(any())).thenReturn(role);
        assertEquals(service.getRoleByRole(userRole).getRole(), role.getRole());
    }
}
