package com.luminabackend.services;

import com.luminabackend.dtos.professor.NewProfessorDTO;
import com.luminabackend.models.professor.Professor;
import com.luminabackend.models.student.Student;
import com.luminabackend.repositories.professor.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository repository;

    public Professor save(NewProfessorDTO newProfessorDTO){
        Professor professor = new Professor(newProfessorDTO);
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
