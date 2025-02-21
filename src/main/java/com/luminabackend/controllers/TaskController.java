package com.luminabackend.controllers;

import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskGetDTO;
import com.luminabackend.models.education.task.TaskPostDTO;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<List<TaskGetDTO>> getAllClassroomTasks(@PathVariable UUID classroomId){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if(classroomById.isPresent()){
            return ResponseEntity.ok(taskService.getAllTasks(classroomId).stream().map(TaskGetDTO::new).toList());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskGetDTO> getClassroomTask(@PathVariable UUID classroomId,
                                                       @PathVariable UUID taskId){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if(classroomById.isPresent()){
            Optional<Task> taskById = taskService.getTaskById(taskId);
            return taskById.map(task ->
                            ResponseEntity.ok(new TaskGetDTO(task)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<TaskGetDTO> createTask(@PathVariable UUID classroomId,
                                                 @Valid @RequestBody TaskPostDTO taskPostDTO) {
        if (classroomService.getClassroomById(classroomId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Task savedTask = taskService.save(taskPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskGetDTO(savedTask));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID classroomId,
                                           @PathVariable UUID taskId) {
        if (classroomService.getClassroomById(classroomId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (taskService.getTaskById(taskId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        taskService.deleteById(taskId);
        return ResponseEntity.noContent().build();
    }
}