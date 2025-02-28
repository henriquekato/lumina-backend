package com.luminabackend.models.education.submission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "submissions")
@Getter @Setter @NoArgsConstructor
public class Submission {
    @Id
    private UUID id;
    private UUID taskId;
    private UUID studentId;
    private String content;
    private String fileId;
    private LocalDateTime submittedAt;
    private double grade;

    public Submission(SubmissionPostDTO submissionPostDTO, UUID taskId, UUID studentId, String fileId) {
        this.id = UUID.randomUUID();
        this.taskId = taskId;
        this.studentId = studentId;
        this.content = submissionPostDTO.content();
        this.fileId = fileId;
        this.submittedAt = LocalDateTime.now();
    }
}
