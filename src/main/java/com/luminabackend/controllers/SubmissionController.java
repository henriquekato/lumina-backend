package com.luminabackend.controllers;

import com.luminabackend.exceptions.MissingFileException;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.services.*;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
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
import java.util.UUID;

@RestController
@RequestMapping("/classroom/{classroomId}/task/{taskId}/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @GetMapping
    public ResponseEntity<List<SubmissionGetDTO>> getAllTaskSubmissions(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        List<SubmissionGetDTO> submissions = submissionService.getAllSubmissions(classroomId, taskId, payloadDTO)
                .stream()
                .map(SubmissionGetDTO::new)
                .toList();
        return ResponseEntity.ok(submissions);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionGetDTO> getTaskSubmissionById(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        Submission submission = submissionService.getSubmissionBasedOnUserPermission(submissionId, classroomId, taskId, payloadDTO);
        return ResponseEntity.ok(new SubmissionGetDTO(submission));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionGetDTO> createSubmission(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestPart("submission") SubmissionPostDTO submissionPostDTO,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        if (file == null || file.isEmpty()) {
            throw new MissingFileException("Missing submission file");
        }

        Submission savedSubmission = submissionService.saveSubmission(classroomId, taskId, payloadDTO, submissionPostDTO, file);
        return ResponseEntity.ok(new SubmissionGetDTO(savedSubmission));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        submissionService.deleteById(submissionId, classroomId, taskId, payloadDTO);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{submissionId}/file/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadSubmissionFile(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @PathVariable String fileId,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        submissionService.checkPermission(classroomId, taskId, submissionId, payloadDTO);

        GridFsResource file = fileStorageService.getFile(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new ByteArrayResource(file.getInputStream().readAllBytes()));
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PutMapping("/{submissionId}")
    public ResponseEntity<SubmissionGetDTO> submissionAssessment(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @Valid @RequestBody SubmissionAssessmentDTO submissionAssessmentDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Submission submission = submissionService.submissionAssessment(submissionId, classroomId, taskId, payloadDTO, submissionAssessmentDTO);
        return ResponseEntity.ok(new SubmissionGetDTO(submission));
    }
}
