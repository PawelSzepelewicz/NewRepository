package com.example.probation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SelectedUsersDto {
    private Long id;
    private String userName;
    private String description;
    private Integer rating;
}
