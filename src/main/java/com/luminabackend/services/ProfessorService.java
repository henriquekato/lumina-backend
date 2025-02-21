package com.luminabackend.services;

import com.luminabackend.models.user.professor.ProfessorPostDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.repositories.ProfessorRepository;
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

    public Professor save(ProfessorPostDTO professorPostDTO){
        String username = professorPostDTO.username().trim();
        String email = professorPostDTO.email().trim();
        String password = professorPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = professorPostDTO.username().trim();
        String lastName = professorPostDTO.username().trim();

        Professor professor = new Professor(username, email, encodedPassword, firstName, lastName);
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
