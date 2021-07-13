package com.example.probation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends AbstractEntity {
    private static final Integer INITIAL_RATING = 2500;

    @NotBlank(message = "{name.notempty}")
    @Size(min = 3, max = 32, message = "{name.size}")
    private String userName;
    @NotBlank(message = "{description.notempty}")
    @Size(min = 1, max = 1000, message = "{description.size}")
    private String description;
    private Integer rating = INITIAL_RATING;
}
