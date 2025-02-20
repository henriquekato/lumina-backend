package com.luminabackend.controllers;

import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomGetDTO;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskGetDTO;
import com.luminabackend.models.education.task.TaskPostDTO;
import com.luminabackend.models.user.student.Student;
import com.luminabackend.models.user.student.StudentGetDTO;
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
@RequestMapping("/classroom")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TaskService taskService;

    @GetMapping
    private ResponseEntity<List<ClassroomGetDTO>> getAllClassrooms() {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        return classrooms.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(classrooms.stream().map(ClassroomGetDTO::new).toList());
    }

    @GetMapping("/{classroomId}")
    private ResponseEntity<ClassroomGetDTO> getClassroom(@PathVariable UUID classroomId) {
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        return classroomById.map(classroom ->
                        ResponseEntity.ok(new ClassroomGetDTO(classroom)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Classroom> saveClassroom(@Valid @RequestBody ClassroomPostDTO classroomPostDTO){
        Classroom classroom = classroomService.save(classroomPostDTO);
        return ResponseEntity.ok(classroom);
    }

    @DeleteMapping("/{classroomId}")
    private ResponseEntity<Classroom> deleteClassroom(@PathVariable UUID classroomId){
        if (!classroomService.existsClassroomById(classroomId)){
            return ResponseEntity.notFound().build();
        }
        classroomService.deleteClassroomById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<?> addStudent(@PathVariable UUID classroomId,
                                        @PathVariable UUID studentId) {
        if(classroomService.studentInClassroom(classroomId, studentId)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already in classroom.");
        }
        return ResponseEntity.ok(classroomService.addStudentToClassroom(classroomId, studentId));
    }

    @DeleteMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Classroom> removeStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(classroomService.removeStudentFromClassroom(classroomId, studentId));
    }

    @PostMapping("/{classroomId}/task")
    public ResponseEntity<TaskGetDTO> createClassroomTask(@PathVariable UUID classroomId, @Valid @RequestBody TaskPostDTO taskPostDTO){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if(classroomById.isPresent()){
            Task save = taskService.save(taskPostDTO);
            return ResponseEntity.ok(new TaskGetDTO(save));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{classroomId}/task")
    public ResponseEntity<List<TaskGetDTO>> getAllClassroomTasks(@PathVariable UUID classroomId){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if(classroomById.isPresent()){
            return ResponseEntity.ok(taskService.getAllTasks().stream().map(TaskGetDTO::new).toList());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{classroomId}/task/{taskId}")
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

    @DeleteMapping("/{classroomId}/task/{taskId}")
    public ResponseEntity<Void> deleteClassroomTask(@PathVariable UUID classroomId,
                                                       @PathVariable UUID taskId){
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        if (classroomById.isPresent()) {
            Optional<Task> taskById = taskService.getTaskById(taskId);
            if (taskById.isPresent()) {
                taskService.deleteById(taskById.get().getId());
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }
}
