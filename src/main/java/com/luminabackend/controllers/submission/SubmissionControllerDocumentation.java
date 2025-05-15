package com.luminabackend.controllers.submission;

import com.luminabackend.models.education.submission.SubmissionAssessmentDTO;
import com.luminabackend.models.education.submission.SubmissionGetDTO;
import com.luminabackend.models.education.submission.SubmissionPostDTO;
import com.luminabackend.exceptions.errors.GeneralErrorResponseDTO;
import com.luminabackend.exceptions.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
public interface SubmissionControllerDocumentation {
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
    ResponseEntity<List<SubmissionGetDTO>> getAllTaskSubmissions(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader);

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
    ResponseEntity<Page<SubmissionGetDTO>> getPaginatedTaskSubmissions(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader);

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
    ResponseEntity<SubmissionGetDTO> getTaskSubmissionById(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @RequestHeader("Authorization") String authorizationHeader);

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
    ResponseEntity<SubmissionGetDTO> createSubmission(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestPart("submission") SubmissionPostDTO submissionPostDTO,
            @RequestPart("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException;

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
    ResponseEntity<Void> deleteSubmission(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @RequestHeader("Authorization") String authorizationHeader);

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
    ResponseEntity<ByteArrayResource> downloadSubmissionFile(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @PathVariable String fileId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException;

    @Operation(summary = "Get all task submissions files")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the zip file",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GridFsResource.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id, task id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) })
    })
    ResponseEntity<ByteArrayResource> downloadAllTaskSubmissionsFiles(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws IOException;

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
    ResponseEntity<SubmissionGetDTO> submissionAssessment(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @PathVariable UUID submissionId,
            @Valid @RequestBody SubmissionAssessmentDTO submissionAssessmentDTO,
            @RequestHeader("Authorization") String authorizationHeader);
}
