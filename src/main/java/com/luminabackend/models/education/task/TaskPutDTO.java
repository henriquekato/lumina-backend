package com.luminabackend.models.education.task;

import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

public record TaskPutDTO(
        String title,

        String description,

        @FutureOrPresent(message = "Due date must be in the future or present")
        LocalDateTime dueDate
) {
}
