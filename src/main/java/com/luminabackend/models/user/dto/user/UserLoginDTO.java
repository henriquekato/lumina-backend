package com.luminabackend.models.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO (
    @Email(message = "Invalid email")
    @NotBlank(message = "Email can not be null")
    String email,

    @NotBlank(message = "Password can not be null")
    String password
) {
}
