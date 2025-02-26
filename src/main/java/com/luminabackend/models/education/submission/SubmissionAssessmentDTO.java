package com.luminabackend.models.education.submission;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SubmissionAssessmentDTO(
    @NotNull(message = "Task ID can not be null")
    UUID taskId,

    @NotNull(message = "Student ID can not be null")
    UUID studentId,

    @NotNull(message = "Grade can not be null")
    double grade
) {
}
