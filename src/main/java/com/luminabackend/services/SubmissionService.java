package com.luminabackend.services;

import com.luminabackend.exceptions.*;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.repositories.submission.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Submission> getAllSubmissionsByTaskId(UUID taskId) {
        return repository.findAllByTaskId(taskId);
    }

    public Page<Submission> getPaginatedTaskSubmissions(UUID taskId, Pageable page) {
        return repository.findAllByTaskId(taskId, page);
    }

    public Submission getSubmissionById(UUID id) {
        Optional<Submission> submissionById = repository.findById(id);
        if (submissionById.isEmpty()) throw new EntityNotFoundException("Submission not found");
        return submissionById.get();
    }

    public ByteArrayResource getAllTaskSubmissionsFiles(UUID taskId) throws IOException {
        List<Submission> submissions = getAllSubmissionsByTaskId(taskId);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

            submissions.forEach(submission -> {
                GridFsResource fileResource = fileStorageService.getFile(submission.getFileId());
                if (fileResource != null) {
                    addSubmissionToZip(submission, fileResource, zipOutputStream);
                }
            });

            zipOutputStream.finish();
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private void addSubmissionToZip(Submission submission, GridFsResource gridFsResource, ZipOutputStream zipOutputStream) {
        try {
            String zipPath = submission.getStudentId() + "/" + gridFsResource.getFilename();

            ZipEntry zipEntry = new ZipEntry(zipPath);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(gridFsResource.getInputStream().readAllBytes());
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new ZipProcessingException("Failed to add file to zip");
        }
    }

    public Submission saveSubmission(UUID taskId, UUID studentId, SubmissionPostDTO submissionPostDTO, MultipartFile file) throws IOException {
        if (repository.existsByStudentIdAndTaskId(studentId, taskId))
            throw new TaskAlreadySubmittedException("You have already submitted this task");

        String fileId = fileStorageService.storeFile(file, taskId);
        Submission submission = new Submission(submissionPostDTO, taskId, studentId, fileId);
        return repository.save(submission);
    }

    public void deleteById(UUID submissionId) {
        Submission submission = getSubmissionById(submissionId);
        if (submission.getFileId() != null)
            fileStorageService.deleteFile(submission.getFileId());
        repository.deleteById(submissionId);
    }

    public void deleteAllByTaskId(UUID taskId) {
        repository.findAllByTaskId(taskId).forEach(s -> {
              if (s.getFileId() != null) {
                  fileStorageService.deleteFile(s.getFileId());
              }
              repository.delete(s);
          });
    }

    public Submission submissionAssessment(UUID submissionId, SubmissionAssessmentDTO submissionAssessmentDTO) {
        Submission submission = getSubmissionById(submissionId);
        submission.setGrade(submissionAssessmentDTO.grade());
        return repository.save(submission);
    }
}
