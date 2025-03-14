package com.luminabackend.models.education.submission;

import jakarta.validation.constraints.NotNull;

public record SubmissionAssessmentDTO(
        @NotNull(message = "Grade can not be null")
        Double grade
) {
}
