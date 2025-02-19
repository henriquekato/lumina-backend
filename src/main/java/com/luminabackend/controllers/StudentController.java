package com.luminabackend.controllers;

import com.luminabackend.dtos.student.NewStudentDTO;
import com.luminabackend.models.student.Student;
import com.luminabackend.dtos.student.StudentDataDTO;
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
    public ResponseEntity<StudentDataDTO> getStudent(@PathVariable UUID id) {
        Optional<Student> studentById = service.getStudentById(id);
        return studentById.map(student ->
                ResponseEntity.ok(new StudentDataDTO(student)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<StudentDataDTO> saveStudent(@Valid @RequestBody NewStudentDTO newStudentDTO) {
        Optional<Student> studentByEmail = service.getStudentByEmail(newStudentDTO.email());
        if (studentByEmail.isEmpty()) {
            Student save = service.save(newStudentDTO);
            return ResponseEntity.ok(new StudentDataDTO(save));
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
