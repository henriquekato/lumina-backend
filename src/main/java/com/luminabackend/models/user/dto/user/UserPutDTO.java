package com.luminabackend.models.user.dto.user;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record UserPutDTO(
        @Email(message = "Invalid email")
        String email,

        @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        String firstName,

        String lastName
) {
}
