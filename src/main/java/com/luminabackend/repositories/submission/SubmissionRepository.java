package com.luminabackend.repositories.submission;

import com.luminabackend.models.education.submission.Submission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface SubmissionRepository extends MongoRepository<Submission, UUID> {
}
