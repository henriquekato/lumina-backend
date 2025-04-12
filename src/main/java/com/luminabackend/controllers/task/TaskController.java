package com.luminabackend.controllers.task;

import com.luminabackend.models.education.task.TaskGetDTO;
import com.luminabackend.models.education.task.*;
import com.luminabackend.services.TaskService;
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

@RestController
@RequestMapping("/classroom/{classroomId}/task")
public class TaskController implements TaskControllerDocumentation{
    @Autowired
    private TaskService taskService;

    @Override
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

    @Override
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @GetMapping
    public ResponseEntity<Page<TaskGetDTO>> getPaginatedClassroomTasks(
            @PathVariable UUID classroomId,
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader){
        Page<Task> tasks = taskService.getPaginatedClassroomTasks(classroomId, page);
        return ResponseEntity.ok(tasks.map(TaskGetDTO::new));
    }

    @Override
    @PreAuthorize("(hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')) and @resourceAccess.verifyClassroomAccess(#authorizationHeader, #classroomId)")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> getClassroomTask(
            @PathVariable UUID classroomId,
            @PathVariable UUID taskId,
            @RequestHeader("Authorization") String authorizationHeader){
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(new TaskGetDTO(task));
    }

    @Override
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

    @Override
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

    @Override
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
