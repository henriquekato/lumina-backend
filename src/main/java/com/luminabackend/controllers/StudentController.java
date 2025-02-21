package com.luminabackend.controllers;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.student.StudentPostDTO;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import com.luminabackend.services.AccountService;
import com.luminabackend.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private StudentService service;

    @Autowired
    private AccountService accountService;

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
    public ResponseEntity<?> saveStudent(@Valid @RequestBody StudentPostDTO studentPostDTO, UriComponentsBuilder uriBuilder) {
        Optional<User> studentByEmail = accountService.getUserByEmail(studentPostDTO.email());

        if (studentByEmail.isPresent()) return ResponseEntity.badRequest().body("This email address is already registered");

        Student newStudent = service.save(studentPostDTO);
        var uri = uriBuilder.path("/user/{id}").buildAndExpand(newStudent.getId()).toUri();
        return ResponseEntity.created(uri).build();
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
