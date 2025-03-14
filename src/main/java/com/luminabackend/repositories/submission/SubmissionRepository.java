package com.luminabackend.repositories.submission;

import com.luminabackend.models.education.submission.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends MongoRepository<Submission, UUID> {
    List<Submission> findAllByTaskId(UUID taskId);
    boolean existsByStudentIdAndTaskId(UUID studentId, UUID taskId);
}
