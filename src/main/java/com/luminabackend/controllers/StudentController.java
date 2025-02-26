package com.luminabackend.controllers;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.AccountService;
import com.luminabackend.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
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
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    @PostMapping
    public ResponseEntity<?> saveStudent(@Valid @RequestBody UserSignupDTO studentPostDTO, UriComponentsBuilder uriBuilder) {
        Optional<User> userByEmail = accountService.getUserByEmail(studentPostDTO.email());

        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        Student newStudent = service.save(studentPostDTO);
        var uri = uriBuilder.path("/student/{id}").buildAndExpand(newStudent.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editStudent(@PathVariable UUID id, @Valid @RequestBody UserPutDTO studentPostDTO) {
        Optional<Student> studentById = service.getStudentById(id);
        if(studentById.isEmpty()) throw new EntityNotFoundException("Student not found");

        Student student = studentById.get();
        String newEmail = studentPostDTO.email();
        if (newEmail != null) {
            newEmail = newEmail.trim();
            Optional<User> user = accountService.getUserByEmail(newEmail);
            if (user.isPresent()) throw new EmailAlreadyInUseException();
            student.setEmail(newEmail);
        }
        if (studentPostDTO.password() != null) {
            student.setPassword(studentPostDTO.password().trim());
        }
        if (studentPostDTO.firstName() != null) {
            student.setFirstName(studentPostDTO.firstName().trim());
        }
        if (studentPostDTO.lastName() != null) {
            student.setLastName(studentPostDTO.lastName().trim());
        }

        service.save(student);
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        if (!service.existsById(id)){
            throw new EntityNotFoundException("Student not found");
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
