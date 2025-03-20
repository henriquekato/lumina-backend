package com.luminabackend.controllers;

import com.luminabackend.models.education.task.TaskGetDTO;
import com.luminabackend.models.education.task.*;
import com.luminabackend.services.TaskService;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import com.luminabackend.utils.errors.ValidationErrorResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@RestController
@RequestMapping("/classroom/{classroomId}/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

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
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @GetMapping("/all")
    public ResponseEntity<List<TaskGetDTO>> getAllClassroomTasks(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader){
        List<TaskGetDTO> tasks = taskService.getAllTasksByClassroomId(classroomId)
                .stream()
                .map(TaskGetDTO::new)
                .toList();
        return ResponseEntity.ok(tasks);
    }

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
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @GetMapping
    public ResponseEntity<Page<TaskGetDTO>> getPaginatedClassroomTasks(
            @PathVariable UUID classroomId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader){
        Page<Task> tasks = taskService.getPaginatedClassroomTasks(classroomId, page);
        return ResponseEntity.ok(tasks.map(TaskGetDTO::new));
    }

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
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> getClassroomTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader){
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(new TaskGetDTO(task));
    }

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
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @PostMapping
    public ResponseEntity<TaskGetDTO> createTask(
            @PathVariable UUID classroomId,
            @Valid @RequestBody TaskPostDTO taskPostDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO(taskPostDTO, classroomId);
        Task savedTask = taskService.save(taskCreateDTO);
        return ResponseEntity
                .created(linkTo(methodOn(TaskController.class)
                        .getClassroomTask(classroomId, savedTask.getId(), authorizationHeader))
                        .toUri())
                .body(new TaskGetDTO(savedTask));
    }

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
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> editTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskPutDTO taskPutDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        Task task = taskService.edit(taskId, taskPutDTO);
        return ResponseEntity.ok(new TaskGetDTO(task));
    }

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
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader) {
        taskService.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }
}
