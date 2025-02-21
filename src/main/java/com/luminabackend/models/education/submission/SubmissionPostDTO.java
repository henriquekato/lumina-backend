package com.luminabackend.models.education.submission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionPostDTO(
        @NotNull(message = "Task ID can not be null")
        UUID taskId,

        @NotNull(message = "Student ID can not be null")
        UUID studentId,

        @NotBlank(message = "Content can not be null")
        String content,

        @NotNull(message = "Submission date can not be null")
        LocalDateTime submittedAt
) {
}
