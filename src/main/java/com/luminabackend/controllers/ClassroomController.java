package com.luminabackend.controllers;

import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomGetDTO;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.services.ClassroomService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ClassroomGetDTO>> getAllClassrooms() {
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        return classrooms.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(classrooms.stream().map(ClassroomGetDTO::new).toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}")
    public ResponseEntity<ClassroomGetDTO> getClassroom(@PathVariable UUID classroomId) {
        Optional<Classroom> classroomById = classroomService.getClassroomById(classroomId);
        return classroomById.map(classroom -> ResponseEntity.ok(new ClassroomGetDTO(classroom)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Classroom> saveClassroom(@Valid @RequestBody ClassroomPostDTO classroomPostDTO) {
        Classroom classroom = classroomService.save(classroomPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(classroom);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable UUID classroomId) {
        if (!classroomService.existsClassroomById(classroomId)) {
            return ResponseEntity.notFound().build();
        }
        classroomService.deleteClassroomById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping("/{classroomId}/students/{studentId}")
    public ResponseEntity<?> addStudent(@PathVariable UUID classroomId,
                                        @PathVariable UUID studentId) {
        if (classroomService.studentInClassroom(classroomId, studentId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already in classroom.");
        }
        return ResponseEntity.ok(classroomService.addStudentToClassroom(classroomId, studentId));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{classroomId}/students/{studentId}")
    public ResponseEntity<?> removeStudent(@PathVariable UUID classroomId,
                                           @PathVariable UUID studentId) {
        return ResponseEntity.ok(classroomService.removeStudentFromClassroom(classroomId, studentId));
    }
}
