package com.luminabackend.controllers.student;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/student")
public class StudentController implements StudentControllerDocumentation {
    @Autowired
    private StudentService service;

    @Override
    @GetMapping("/all")
    public ResponseEntity<List<StudentGetDTO>> getAllStudents() {
        List<Student> students = service.getAllStudents();
        return ResponseEntity.ok(students.stream().map(StudentGetDTO::new).toList());
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<StudentGetDTO>> getPaginatedStudents(Pageable page) {
        Page<Student> students = service.getPaginatedStudents(page);
        return ResponseEntity.ok(students.map(StudentGetDTO::new));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<StudentGetDTO> getStudent(@PathVariable UUID id) {
        Optional<Student> studentById = service.getStudentById(id);
        return studentById.map(student ->
                        ResponseEntity.ok(new StudentGetDTO(student)))
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
    }

    @Override
    @PostMapping
    public ResponseEntity<StudentGetDTO> saveStudent(@Valid @RequestBody UserSignupDTO studentPostDTO) {
        Student newStudent = service.save(studentPostDTO);
        return ResponseEntity
                .created(linkTo(methodOn(StudentController.class)
                        .getStudent(newStudent.getId()))
                        .toUri())
                .body(new StudentGetDTO(newStudent));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<StudentGetDTO> editStudent(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO) {
        Student student = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new StudentGetDTO(student));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
