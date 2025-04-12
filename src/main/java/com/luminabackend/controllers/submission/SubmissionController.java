package com.luminabackend.controllers.submission;

import com.luminabackend.exceptions.MissingFileException;
import com.luminabackend.exceptions.TaskDueDateExpiredException;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.services.*;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/classroom/{classroomId}/task/{taskId}/submission")
public class SubmissionController implements SubmissionControllerDocumentation {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AccessService accessService;

    @Override
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR')) " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId)")
    @GetMapping("/all")
    public ResponseEntity<List<SubmissionGetDTO>> getAllTaskSubmissions(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader){
        List<SubmissionGetDTO> submissions = submissionService.getAllSubmissionsByTaskId(taskId)
                .stream()
                .map(SubmissionGetDTO::new)
                .toList();
        return ResponseEntity.ok(submissions);
    }

    @Override
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR')) " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId)")
    @GetMapping
    public ResponseEntity<Page<SubmissionGetDTO>> getPaginatedTaskSubmissions(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader){
        Page<Submission> submissions = submissionService.getPaginatedTaskSubmissions(taskId, page);
        return ResponseEntity.ok(submissions.map(SubmissionGetDTO::new));
    }

    @Override
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId)")
    @GetMapping("/{submissionId}")
    public ResponseEntity<SubmissionGetDTO> getTaskSubmissionById(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        accessService.checkStudentAccessToSubmissionById(submissionId, new UserAccessDTO(payloadDTO));
        Submission submission = submissionService.getSubmissionById(submissionId);
        return ResponseEntity.ok(new SubmissionGetDTO(submission));
    }

    @Override
    @PreAuthorize("hasRole('STUDENT') " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionGetDTO> createSubmission(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestPart("submission") SubmissionPostDTO submissionPostDTO,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        taskService.isDueDateExpired(taskId);

        if (file == null || file.isEmpty())
            throw new MissingFileException("Missing submission file");

        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Submission savedSubmission = submissionService.save(taskId, payloadDTO.id(), submissionPostDTO, file);

        return ResponseEntity
                .created(linkTo(methodOn(SubmissionController.class)
                        .getTaskSubmissionById(classroomId, taskId, savedSubmission.getId(), authorizationHeader))
                        .toUri())
                .body(new SubmissionGetDTO(savedSubmission));
    }

    @Override
    @PreAuthorize("hasRole('STUDENT') " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId) " +
            "and @resourceAccess.verifySubmissionAccess(#authorizationHeader, #submissionId)")
    @DeleteMapping("/{submissionId}")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @RequestHeader("Authorization") String authorizationHeader) {
        taskService.isDueDateExpired(taskId);
        submissionService.deleteById(submissionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId) " +
            "and @resourceAccess.verifySubmissionAccess(#authorizationHeader, #submissionId)")
    @GetMapping("/{submissionId}/file/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadSubmissionFile(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @PathVariable String fileId,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        GridFsResource file = fileStorageService.getFile(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(new ByteArrayResource(file.getInputStream().readAllBytes()));
    }

    @Override
    @PreAuthorize("hasRole('PROFESSOR') " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId)")
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadAllTaskSubmissionsFiles(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader) throws IOException {
        ByteArrayResource zipFile = submissionService.getAllTaskSubmissionsFiles(taskId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"task-" + taskId + ".zip\"")
                .body(zipFile);
    }

    @Override
    @PreAuthorize("hasRole('PROFESSOR') " +
            "and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId) " +
            "and @taskExistence.verify(#taskId)")
    @PutMapping("/{submissionId}")
    public ResponseEntity<SubmissionGetDTO> submissionAssessment(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @Valid @RequestBody SubmissionAssessmentDTO submissionAssessmentDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        Submission submission = submissionService.submissionAssessment(submissionId, submissionAssessmentDTO);
        return ResponseEntity.ok(new SubmissionGetDTO(submission));
    }
}
