package com.luminabackend.services;

import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.repositories.submission.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository repository;

    public Submission save(SubmissionPostDTO submissionPostDTO) {
        Submission submission = new Submission(submissionPostDTO);
        return repository.save(submission);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public List<Submission> getAllSubmissions(UUID taskId) {
        return repository.findAll().stream().filter(s -> s.getTaskId().equals(taskId)).toList();
    }

    public Optional<Submission> getSubmissionById(UUID id) {
        return repository.findById(id);
    }
}
