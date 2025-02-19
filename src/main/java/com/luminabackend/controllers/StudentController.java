package com.luminabackend.controllers;

import com.luminabackend.dtos.student.StudentDTO;
import com.luminabackend.models.student.Student;
import com.luminabackend.models.student.StudentData;
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
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = service.getAllStudents();
        return students.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(students);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<StudentData> getStudent(@PathVariable UUID id) {
        Optional<Student> studentById = service.getStudentById(id);
        return studentById.map(student ->
                ResponseEntity.ok(new StudentData(student)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<StudentData> saveStudent(@Valid @RequestBody StudentDTO studentDTO) {
        Optional<Student> studentByEmail = service.getStudentByEmail(studentDTO.email());
        if (studentByEmail.isEmpty()) {
            Student save = service.save(studentDTO);
            return ResponseEntity.ok(new StudentData(save));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable UUID id) {
        if (!service.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
