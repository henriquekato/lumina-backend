package com.luminabackend.dtos.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record StudentDTO(
        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @Length(min = 6, max = 20)
        String password
) {
}
