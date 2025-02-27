package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskGetDTO;
import com.luminabackend.models.education.task.TaskPostDTO;
import com.luminabackend.models.education.task.TaskPutDTO;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/classroom/{classroomId}/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassroomService classroomService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<TaskGetDTO>> getAllClassroomTasks(@PathVariable UUID classroomId){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if(classroomById.isEmpty())
            throw new EntityNotFoundException("Classroom not found");
        return ResponseEntity.ok(taskService.getAllTasks(classroomId).stream().map(TaskGetDTO::new).toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> getClassroomTask(@PathVariable UUID classroomId,
                                                       @PathVariable UUID taskId){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if(classroomById.isEmpty()) throw new EntityNotFoundException("Classroom not found");

        Optional<Task> taskById = taskService.getTaskById(taskId);
        return taskById.map(task ->
                        ResponseEntity.ok(new TaskGetDTO(task)))
                        .orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping
    public ResponseEntity<TaskGetDTO> createTask(@PathVariable UUID classroomId,
                                                 @Valid @RequestBody TaskPostDTO taskPostDTO,
                                                 UriComponentsBuilder uriBuilder) {
        if (classroomService.getClassroomById(classroomId).isEmpty())
            throw new EntityNotFoundException("Classroom not found");

        Task savedTask = taskService.save(taskPostDTO);
        var uri = uriBuilder.path("/classroom/{classroomId}/task/{id}").buildAndExpand(savedTask.getClassroomId(), savedTask.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editTask(@PathVariable UUID id, @Valid @RequestBody TaskPutDTO taskPutDTO) {
        Optional<Task> taskById = taskService.getTaskById(id);
        if(taskById.isEmpty())
            throw new EntityNotFoundException("Task not found");

        Task task = taskById.get();
        if (taskPutDTO.title() != null) {
            task.setTitle(taskPutDTO.title().trim());
        }
        if (taskPutDTO.description() != null) {
            task.setDescription(taskPutDTO.description().trim());
        }
        if (taskPutDTO.dueDate() != null) {
            task.setDueDate(taskPutDTO.dueDate());
        }

        taskService.save(task);
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID classroomId,
                                           @PathVariable UUID taskId) {
        if (classroomService.getClassroomById(classroomId).isEmpty())
            throw new EntityNotFoundException("Classroom not found");

        if (taskService.getTaskById(taskId).isEmpty())
            throw new EntityNotFoundException("Task not found");

        taskService.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }
}
