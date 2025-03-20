package com.luminabackend.controllers.task;

import com.luminabackend.models.education.task.*;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                description = "Access denied to this classroom",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
        @ApiResponse(
                responseCode = "404",
                description = "Classroom not found",
                content = { @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
})
public interface TaskControllerDocumentation {
    @Operation(summary = "Get a list of classroom tasks based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a list of tasks of the classroom",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<List<TaskGetDTO>> getAllClassroomTasks(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Get a paginated list of classroom tasks based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns a paginated list of classroom tasks",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<Page<TaskGetDTO>> getPaginatedClassroomTasks(
            @PathVariable UUID classroomId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Get a task by its id based on user access")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Returns the specified task",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or task id",
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
    ResponseEntity<TaskGetDTO> getClassroomTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Create a new task")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully create a task",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail on request body validation",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)) }),
    })
    ResponseEntity<TaskGetDTO> createTask(
            @PathVariable UUID classroomId,
            @Valid @RequestBody TaskPostDTO taskPostDTO,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Edit a task by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully edit the task",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskGetDTO.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or task id",
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
                    description = "Task not found",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GeneralErrorResponseDTO.class)) }),
    })
    ResponseEntity<TaskGetDTO> editTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskPutDTO taskPutDTO,
            @RequestHeader("Authorization") String authorizationHeader);

    @Operation(summary = "Delete a task and its dependencies by task id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successfully delete the task"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid classroom id or task id",
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
    ResponseEntity<Void> deleteTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader);
}
