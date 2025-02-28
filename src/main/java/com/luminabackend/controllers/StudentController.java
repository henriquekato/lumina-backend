package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService service;

    @GetMapping
    public ResponseEntity<List<StudentGetDTO>> getAllStudents() {
        List<Student> students = service.getAllStudents();
        return ResponseEntity.ok(students.stream().map(StudentGetDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentGetDTO> getStudent(@PathVariable UUID id) {
        Optional<Student> studentById = service.getStudentById(id);
        return studentById.map(student ->
                        ResponseEntity.ok(new StudentGetDTO(student)))
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    @PostMapping
    public ResponseEntity<StudentGetDTO> saveStudent(@Valid @RequestBody UserSignupDTO studentPostDTO) {
        Student newStudent = service.save(studentPostDTO);
        return ResponseEntity.ok(new StudentGetDTO(newStudent));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentGetDTO> editStudent(@PathVariable UUID id, @Valid @RequestBody UserPutDTO userPutDTO) {
        Student student = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new StudentGetDTO(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
