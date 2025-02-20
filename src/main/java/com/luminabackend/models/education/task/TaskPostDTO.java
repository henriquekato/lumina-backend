package com.luminabackend.models.education.task;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskPostDTO(
        @NotBlank(message = "Title can not be null")
        String title,

        @NotBlank(message = "Description can not be null")
        String description,

        @NotNull(message = "Due date can not be null")
        @FutureOrPresent(message = "Due date must be in the future or present")
        LocalDateTime dueDate,

        @NotNull(message = "Classroom ID can not be null")
        UUID classroomId
){
}
