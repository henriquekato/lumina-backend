package com.luminabackend.models.user.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record StudentPostDTO(
        @NotBlank(message = "Name can not be null")
        String name,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email can not be null")
        String email,

        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        @NotBlank(message = "Password can not be null")
        String password
) {
}
