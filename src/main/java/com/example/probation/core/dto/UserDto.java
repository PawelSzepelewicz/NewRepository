package com.example.probation.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String username;
    private String description;
    private Integer rating;
    private String email;
    private Set<RoleDto> roles;
}
