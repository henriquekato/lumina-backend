package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.*;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Role;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.SubmissionService;
import com.luminabackend.services.TaskService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private TokenService tokenService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SubmissionService submissionService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<ClassroomGetDTO>> getAllClassrooms(@RequestHeader("Authorization") String authorizationHeader) {
        String tokenJWT = authorizationHeader.replace("Bearer ", "");
        PayloadDTO payload = tokenService.getPayloadFromToken(tokenJWT);
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        List<Classroom> filteredClassrooms;

        if (payload.role().equals(Role.ADMIN)) {
            filteredClassrooms = classrooms;
        }
        else if (payload.role().equals(Role.PROFESSOR)) {
            filteredClassrooms = classrooms.stream()
                    .filter(c -> c.getProfessorId().equals(payload.id()))
                    .toList();
        }
        else {
            filteredClassrooms = classrooms.stream()
                    .filter(c -> c.getStudentsIds().contains(payload.id()))
                    .toList();
        }
        return filteredClassrooms.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(filteredClassrooms.stream().map(ClassroomGetDTO::new).toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}")
    public ResponseEntity<?> getClassroom(@PathVariable UUID classroomId,
                                        @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payload = tokenService.getPayloadFromToken(authorizationHeader);
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);

        if (payload.role().equals(Role.ADMIN)) {
            return classroomById.map(classroom -> ResponseEntity.ok(new ClassroomResourceDTO(classroom)))
                    .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
        }
        return classroomById
                .map(classroom -> {
                if (classroom.getProfessorId().equals(payload.id())
                    || classroom.getStudentsIds().contains(payload.id()))
                    return ResponseEntity.ok(new ClassroomResourceDTO(classroom));
                else return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                })
                .orElseThrow(() -> new EntityNotFoundException("Classroom not found"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassroomResourceDTO> saveClassroom(@Valid @RequestBody
                                                   ClassroomPostDTO classroomPostDTO) {
        Classroom savedClassroom = classroomService.save(classroomPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClassroomResourceDTO(savedClassroom));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editClassroom(@PathVariable UUID id, @Valid @RequestBody ClassroomPutDTO classroomPutDTO) {
        Optional<Classroom> classroomById = classroomService.getClassroomById(id);
        if(classroomById.isEmpty()) throw new EntityNotFoundException("Classroom not found");

        Classroom classroom = classroomById.get();
        if (classroomPutDTO.name() != null) {
            classroom.setName(classroomPutDTO.name().trim());
        }
        if (classroomPutDTO.description() != null) {
            classroom.setDescription(classroomPutDTO.description().trim());
        }

        classroomService.save(classroom);
        return ResponseEntity.ok(new ClassroomResourceDTO(classroom));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable UUID classroomId) {
        if (!classroomService.existsClassroomById(classroomId)) {
            throw new EntityNotFoundException("Classroom not found");
        }
        // tasks
        taskService.deleteAll(classroomId);
        // submissions
        submissionService.deleteAll(classroomId);
        // materials

        classroomService.deleteClassroomById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<?> addStudent(@PathVariable UUID classroomId,
                                        @PathVariable UUID studentId) {
        if (classroomService.studentInClassroom(classroomId, studentId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already in classroom.");
        }
        return ResponseEntity.ok(classroomService.addStudentToClassroom(classroomId, studentId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<?> removeStudent(@PathVariable UUID classroomId,
                                           @PathVariable UUID studentId) {
        return ResponseEntity.ok(classroomService.removeStudentFromClassroom(classroomId, studentId));
    }
}
