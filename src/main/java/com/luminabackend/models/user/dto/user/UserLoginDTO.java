package com.luminabackend.models.user.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO (
    @NotBlank(message = "Username can not be null")
    String username,
    @NotBlank(message = "Password can not be null")
    String password
) {
}
