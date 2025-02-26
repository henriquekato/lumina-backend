package com.luminabackend.services;

import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.repositories.submission.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    public Submission saveSubmission(SubmissionPostDTO submissionPostDTO, MultipartFile file) throws IOException {
        String fileId = fileStorageService.storeFile(file, submissionPostDTO.taskId());
        Submission submission = new Submission(submissionPostDTO, fileId);
        return repository.save(submission);
    }

    public Submission submissionAssessment(Submission submission) {
        return repository.save(submission);
    }

    public void deleteById(UUID id) {
        Optional<Submission> submission = repository.findById(id);
        submission.ifPresent(s -> {
            if (s.getFileId() != null) fileStorageService.deleteFile(s.getFileId());
            repository.delete(s);
        });
        repository.deleteById(id);
    }

    public void deleteAll(UUID classroomId) {
          getAllSubmissions(classroomId).forEach(s -> {
              fileStorageService.deleteAll(s.getId());
              repository.delete(s);
          });
    }

    public List<Submission> getAllSubmissions(UUID taskId) {
        return repository.findAll().stream().filter(s -> s.getTaskId().equals(taskId)).toList();
    }

    public Optional<Submission> getSubmissionById(UUID id) {
        return repository.findById(id);
    }
}
