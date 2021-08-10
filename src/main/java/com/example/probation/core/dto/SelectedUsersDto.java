package com.example.probation.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SelectedUsersDto {
    private Long id;
    private String username;
    private String description;
    private Integer rating;
}
