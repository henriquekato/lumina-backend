package com.luminabackend.controllers;

import com.luminabackend.exceptions.MissingFileException;
import com.luminabackend.models.education.submission.Submission;
import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.services.*;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import com.luminabackend.utils.security.PayloadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@ApiResponses(value = {
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized. Incorrect or invalid credentials",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
        @ApiResponse(
                responseCode = "403",
                description = "Access denied to this resource",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
        @ApiResponse(
                responseCode = "404",
                description = "Classroom not found",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
        @ApiResponse(
                responseCode = "404",
                description = "Task not found",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
@RestController
@RequestMapping("/classroom/{classroomId}/task/{taskId}/submission")
public class SubmissionController {
    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "Get a list of submissions from a classroom task")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of submissions",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or task id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @GetMapping("/all")
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

    @Operation(summary = "Get a paginated list of submissions from a classroom task")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of submissions",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or task id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @GetMapping
    public ResponseEntity<Page<SubmissionGetDTO>> getPaginatedTaskSubmissions(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);

        Page<Submission> submissions = submissionService.getPaginatedTaskSubmissions(classroomId, taskId, payloadDTO, page);
        return ResponseEntity.ok(submissions.map(SubmissionGetDTO::new));
    }

    @Operation(summary = "Get a task submission")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the task submission",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, task id or submission id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
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

    @Operation(summary = "Create a new task submission")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a task submission",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or task id",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class))}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task due date expired",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request part validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "409",
                    description = "Submission already sent to this task",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
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
        return ResponseEntity
                .created(linkTo(methodOn(SubmissionController.class)
                        .getTaskSubmissionById(classroomId, taskId, savedSubmission.getId(), authorizationHeader))
                        .toUri())
                .body(new SubmissionGetDTO(savedSubmission));
    }

    @Operation(summary = "Delete a task submission")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the task submission"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, task id or submission id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task due date expired",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
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

    @Operation(summary = "Get a submission file")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the submission file",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GridFsResource.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, task id, submission id, or file id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "File not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
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

    @Operation(summary = "Evaluate a task submission")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully evaluate the task submission",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SubmissionGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, task id or submission id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "404",
                    description = "Submission not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
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
