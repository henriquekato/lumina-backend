package com.luminabackend.dtos.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record NewProfessorDTO(
        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @Length(min = 6, max = 20)
        String password
) {
}
