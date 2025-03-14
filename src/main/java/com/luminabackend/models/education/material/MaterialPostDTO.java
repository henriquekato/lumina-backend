package com.luminabackend.models.education.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MaterialPostDTO(
        @NotNull(message = "Professor ID can not be null")
        UUID professorId,

        @NotBlank(message = "Title can not be null")
        String title,

        String description
) {
}