package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/professor")
public class ProfessorController {
    @Autowired
    private ProfessorService service;

    @GetMapping
    public ResponseEntity<List<ProfessorGetDTO>> getAllProfessors() {
        List<Professor> professors = service.getAllProfessors();
        return ResponseEntity.ok(professors.stream().map(ProfessorGetDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> getProfessor(@PathVariable UUID id) {
        Optional<Professor> professorById = service.getProfessorById(id);
        return professorById.map(professor ->
                        ResponseEntity.ok(new ProfessorGetDTO(professor)))
                .orElseThrow(() -> new EntityNotFoundException(("Professor not found")));
    }

    @PostMapping
    public ResponseEntity<ProfessorGetDTO> saveProfessor(@Valid @RequestBody UserSignupDTO professorPostDTO) {
        Professor newProfessor = service.save(professorPostDTO);
        return ResponseEntity.ok(new ProfessorGetDTO(newProfessor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> editProfessor(@PathVariable UUID id, @Valid @RequestBody UserPutDTO userPutDTO) {
        Professor professor = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new ProfessorGetDTO(professor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
