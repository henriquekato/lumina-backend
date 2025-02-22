package com.luminabackend.controllers;

import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.SubmissionService;
import com.luminabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionGetDTO> getTaskSubmissionById(@PathVariable UUID submissionId) {
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);

        return submission.map(value -> ResponseEntity.ok(new SubmissionGetDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<SubmissionGetDTO>> getAllTaskSubmissions(@PathVariable UUID classroomId,
                                                                    @PathVariable UUID taskId) {
        if (classroomService.getClassroomById(classroomId).isEmpty() ||
                taskService.getTaskById(taskId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<SubmissionGetDTO> submissions = submissionService.getAllSubmissions(taskId)
                .stream()
                .map(SubmissionGetDTO::new)
                .toList();
        return ResponseEntity.ok(submissions);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping
    public ResponseEntity<SubmissionGetDTO> createSubmission(@PathVariable UUID classroomId,
                                                             @PathVariable UUID taskId,
                                                             @RequestBody SubmissionPostDTO submissionPostDTO) {
        if (classroomService.getClassroomById(classroomId).isEmpty() ||
                taskService.getTaskById(taskId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Submission submission = submissionService.save(submissionPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SubmissionGetDTO(submission));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable UUID submissionId) {
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);

        if (submission.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        submissionService.deleteById(submissionId);
        return ResponseEntity.noContent().build();
    }

}
