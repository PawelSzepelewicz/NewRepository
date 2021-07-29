package com.example.probation.model;

import com.example.probation.validatiion.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateUserDto {
    @NotBlank(message = "{name.notempty}")
    @Size(min = 3, max = 32, message = "{name.size}")
    private String username;
    @NotBlank(message = "{description.notempty}")
    @Size(min = 1, max = 1000, message = "{description.size}")
    private String description;
    @NotBlank(message = "{password.notempty}")
    @Password
    private String password;
}
