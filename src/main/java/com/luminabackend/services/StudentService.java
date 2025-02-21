package com.luminabackend.services;

import com.luminabackend.models.user.student.StudentPostDTO;
import com.luminabackend.models.user.Student;
import com.luminabackend.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private StudentRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Student save(StudentPostDTO studentPostDTO) {
        String username = studentPostDTO.username().trim();
        String email = studentPostDTO.email().trim();
        String password = studentPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = studentPostDTO.username().trim();
        String lastName = studentPostDTO.username().trim();

        Student student = new Student(username, email, encodedPassword, firstName, lastName);
        return repository.save(student);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Optional<Student> getStudentById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Student> getStudentByEmail(String email){
        return repository.findByEmail(email);
    }

}
