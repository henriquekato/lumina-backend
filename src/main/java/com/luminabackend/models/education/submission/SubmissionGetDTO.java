package com.luminabackend.models.education.submission;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionGetDTO(
        UUID id,
        UUID taskId,
        UUID studentId,
        String content,
        String fileId,
        LocalDateTime submittedAt,
        Double grade
) {
    public SubmissionGetDTO(Submission submission){
        this(
                submission.getId(),
                submission.getTaskId(),
                submission.getStudentId(),
                submission.getContent(),
                submission.getFileId(),
                LocalDateTime.now(),
                submission.getGrade());
    }
}
