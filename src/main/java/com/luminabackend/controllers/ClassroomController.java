package com.luminabackend.controllers;

import com.luminabackend.models.education.Classroom;
import com.luminabackend.models.education.ClassroomGetDTO;
import com.luminabackend.models.education.ClassroomPostDTO;
import com.luminabackend.models.user.professor.Professor;
import com.luminabackend.models.user.professor.ProfessorGetDTO;
import com.luminabackend.services.ClassroomService;
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
    private ClassroomService service;

    @GetMapping
    private ResponseEntity<List<ClassroomGetDTO>> getAllClassrooms() {
        List<Classroom> classrooms = service.getAllClassrooms();
        return classrooms.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(classrooms.stream().map(ClassroomGetDTO::new).toList());
    }

    @GetMapping("/{classroomId}")
    private ResponseEntity<ClassroomGetDTO> getClassroom(@PathVariable UUID classroomId) {
        Optional<Classroom> classroomById = service.getClassroomById(classroomId);
        return classroomById.map(classroom ->
                        ResponseEntity.ok(new ClassroomGetDTO(classroom)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Classroom> saveClassroom(@Valid @RequestBody ClassroomPostDTO classroomPostDTO){
        Classroom classroom = service.save(classroomPostDTO);
        return ResponseEntity.ok(classroom);
    }

    @DeleteMapping("/{classroomId}")
    private ResponseEntity<Classroom> deleteClassroom(@PathVariable UUID classroomId){
        if (!service.existsClassroomById(classroomId)){
            return ResponseEntity.notFound().build();
        }
        service.deleteClassroomById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<?> addStudent(@PathVariable UUID classroomId,
                                                @PathVariable UUID studentId) {
        if(service.studentInClassroom(classroomId, studentId)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Student already in classroom.");
        }
        return ResponseEntity.ok(service.addStudentToClassroom(classroomId, studentId));
    }

    @DeleteMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Classroom> removeStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId) {
        return ResponseEntity.ok(service.removeStudentFromClassroom(classroomId, studentId));
    }
}
