package com.luminabackend.models.user.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ProfessorPostDTO(
        @NotBlank(message = "First name can not be null")
        String firstName,

        @NotBlank(message = "Last name can not be null")
        String lastName,

        @NotBlank(message = "Username can not be null")
        String username,

        @Email(message = "Invalid email")
        @NotBlank(message = "Email can not be null")
        String email,

        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        @NotBlank(message = "Password can not be null")
        String password
) {
}
