package com.luminabackend.controllers;

import com.luminabackend.models.user.student.StudentPostDTO;
import com.luminabackend.models.user.student.Student;
import com.luminabackend.models.user.student.StudentGetDTO;
import com.luminabackend.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService service;

    @GetMapping
    public ResponseEntity<List<StudentGetDTO>> getAllStudents() {
        List<Student> students = service.getAllStudents();
        return students.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(students.stream().map(StudentGetDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentGetDTO> getStudent(@PathVariable UUID id) {
        Optional<Student> studentById = service.getStudentById(id);
        return studentById.map(student ->
                ResponseEntity.ok(new StudentGetDTO(student)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<?> saveStudent(@Valid @RequestBody StudentPostDTO studentPostDTO) {
        Optional<Student> studentByEmail = service.getStudentByEmail(studentPostDTO.email());

        if (studentByEmail.isPresent()) return ResponseEntity.badRequest().body("This email address is already registered");

        Student save = service.save(studentPostDTO);
        return ResponseEntity.ok(new StudentGetDTO(save));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        if (!service.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
