package com.luminabackend.models.education.classroom;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ClassroomPostDTO(
        @NotBlank(message = "Name can not be null")
        String name,

        String description,

        @NotNull(message = "Professor ID can not be null")
        UUID professorId
) {
}
