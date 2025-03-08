package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.student.StudentRepository;
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
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClassroomService classroomService;

    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    public Optional<Student> getStudentById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Student> getStudentByEmail(String email){
        return repository.findByEmail(email);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Student save(UserSignupDTO studentPostDTO) {
        Optional<User> userByEmail = accountService.getUserByEmail(studentPostDTO.email());

        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        String email = studentPostDTO.email().trim();
        String password = studentPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = studentPostDTO.firstName().trim();
        String lastName = studentPostDTO.lastName().trim();

        Student student = new Student(email, encodedPassword, firstName, lastName);
        return repository.save(student);
    }

    public Student edit(UUID id, UserPutDTO userPutDTO) {
        Optional<Student> studentById = getStudentById(id);
        if (studentById.isEmpty()) throw new EntityNotFoundException("Student not found");

        Student student = studentById.get();
        student = (Student) accountService.editUserData(student, userPutDTO);
        return repository.save(student);
    }

    public void deleteById(UUID id) {
        if (!existsById(id)) {
            throw new EntityNotFoundException("Student not found");
        }

        classroomService.removeStudentFromAllClassrooms(id);

        repository.deleteById(id);
    }
}
