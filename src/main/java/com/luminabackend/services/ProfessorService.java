package com.luminabackend.services;

import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.professor.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Professor save(UserSignupDTO professorPostDTO){
        String email = professorPostDTO.email().trim();
        String password = professorPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = professorPostDTO.firstName().trim();
        String lastName = professorPostDTO.lastName().trim();

        Professor professor = new Professor(email, encodedPassword, firstName, lastName);
        return repository.save(professor);
    }

    public Professor save(Professor professor){
        return repository.save(professor);
    }

    public List<Professor> getAllProfessors() {
        return repository.findAll();
    }

    public Optional<Professor> getProfessorById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Professor> getProfessorByEmail(String email){
        return repository.findByEmail(email);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
