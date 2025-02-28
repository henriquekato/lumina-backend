package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.FileStorageService;
import com.luminabackend.services.SubmissionService;
import com.luminabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/classroom/{classroomId}/task/{taskId}/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<SubmissionGetDTO>> getAllTaskSubmissions(@PathVariable UUID classroomId,
                                                                        @PathVariable UUID taskId) {
        if (!classroomService.existsById(classroomId))
            throw new EntityNotFoundException("Classroom not found");
        if(!taskService.existsById(taskId))
            throw new EntityNotFoundException("Task not found");

        List<SubmissionGetDTO> submissions = submissionService.getAllSubmissions(taskId)
                .stream()
                .map(SubmissionGetDTO::new)
                .toList();
        return ResponseEntity.ok(submissions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionGetDTO> getTaskSubmissionById(@PathVariable UUID submissionId) {
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);

        return submission.map(value -> ResponseEntity.ok(new SubmissionGetDTO(value)))
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionGetDTO> createSubmission(@PathVariable UUID classroomId,
                                                             @PathVariable UUID taskId,
                                                             @RequestPart("submission") SubmissionPostDTO submissionPostDTO,
                                                             @RequestPart("file") MultipartFile file
                                                             ) throws IOException {
        if (!classroomService.existsById(classroomId))
            throw new EntityNotFoundException("Classroom not found");

        if(!taskService.existsById(taskId))
            throw new EntityNotFoundException("Task not found");

        Submission savedSubmission = submissionService.saveSubmission(submissionPostDTO, file);

        return ResponseEntity.ok(new SubmissionGetDTO(savedSubmission));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable UUID submissionId) {
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);
        if (submission.isEmpty()) throw new EntityNotFoundException("Submission not found");

        submissionService.deleteById(submissionId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{submissionId}/file/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadSubmissionFile(@PathVariable String fileId) throws IOException {
        GridFsResource file = fileStorageService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new ByteArrayResource(file.getInputStream().readAllBytes()));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PutMapping("/{submissionId}")
    public ResponseEntity<Void> submissionAssessment(@PathVariable UUID submissionId,
                                                     SubmissionAssessmentDTO submissionAssessmentDTO) {
        Optional<Submission> submissionById = submissionService.getSubmissionById(submissionId);

        if (submissionById.isEmpty()) throw new EntityNotFoundException("Submission not found");

        Submission submission = submissionById.get();
        submission.setGrade(submissionAssessmentDTO.grade());

        submissionService.submissionAssessment(submission);
        return ResponseEntity.noContent().build();
    }
}
