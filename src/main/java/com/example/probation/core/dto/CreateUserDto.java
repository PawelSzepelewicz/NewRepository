package com.example.probation.core.dto;

import com.example.probation.validatiion.annotation.Password;
import com.example.probation.validatiion.annotation.UniqueEmail;
import com.example.probation.validatiion.annotation.UniqueUsername;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    @UniqueUsername
    @NotBlank(message = "{name.notempty}")
    @Size(min = 3, max = 32, message = "{name.size}")
    private String username;
    @NotBlank(message = "{description.notempty}")
    @Size(min = 1, max = 1000, message = "{description.size}")
    private String description;
    @NotBlank(message = "{password.notempty}")
    @Password
    private String password;
    @Email
    @NotBlank(message = "{email.notempty}")
    @UniqueEmail
    private String email;
}
