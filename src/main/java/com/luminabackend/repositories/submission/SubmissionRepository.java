package com.luminabackend.repositories.submission;

import com.luminabackend.models.education.submission.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends MongoRepository<Submission, UUID> {
    List<Submission> findAllByTaskId(UUID taskId);
    Page<Submission> findAllByTaskId(UUID taskId, Pageable page);
    boolean existsByStudentIdAndTaskId(UUID studentId, UUID taskId);
}
