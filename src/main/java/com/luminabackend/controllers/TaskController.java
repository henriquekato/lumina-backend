package com.luminabackend.controllers;

import com.luminabackend.models.education.task.*;
import com.luminabackend.services.TaskService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classroom/{classroomId}/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<TaskGetDTO>> getAllClassroomTasks(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        List<TaskGetDTO> tasks = taskService.getAllTasks(classroomId, payloadDTO).stream().map(TaskGetDTO::new).toList();
        return ResponseEntity.ok(tasks);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> getClassroomTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader){
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Task task = taskService.getTaskBasedOnUserPermission(taskId, classroomId, payloadDTO);
        return ResponseEntity.ok(new TaskGetDTO(task));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping
    public ResponseEntity<TaskGetDTO> createTask(
            @PathVariable UUID classroomId,
            @Valid @RequestBody TaskPostDTO taskPostDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        TaskCreateDTO taskCreateDTO = new TaskCreateDTO(taskPostDTO, classroomId);
        Task savedTask = taskService.save(classroomId, payloadDTO, taskCreateDTO);
        return ResponseEntity.ok(new TaskGetDTO(savedTask));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> editTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @Valid @RequestBody TaskPutDTO taskPutDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Task task = taskService.edit(taskId, classroomId, payloadDTO, taskPutDTO);
        return ResponseEntity.ok(new TaskGetDTO(task));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        taskService.deleteById(taskId, classroomId, payloadDTO);
        return ResponseEntity.noContent().build();
    }
}
