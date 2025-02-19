package com.luminabackend.services;

import com.luminabackend.dtos.student.StudentDTO;
import com.luminabackend.models.student.Student;
import com.luminabackend.models.student.StudentData;
import com.luminabackend.repositories.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {
    @Autowired
    private StudentRepository repository;

    public Student save(StudentDTO studentDTO) {
        Student student = new Student(studentDTO);
        repository.save(student);
        return student;
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
