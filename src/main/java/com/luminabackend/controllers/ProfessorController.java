package com.luminabackend.controllers;

import com.luminabackend.dtos.professor.NewProfessorDTO;
import com.luminabackend.dtos.professor.ProfessorDataDTO;
import com.luminabackend.models.professor.Professor;
import com.luminabackend.services.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/professor")
public class ProfessorController {
    @Autowired
    private ProfessorService service;

    @GetMapping
    public ResponseEntity<List<Professor>> getAllProfessors() {
        List<Professor> professors = service.getAllProfessors();
        return professors.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(professors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorDataDTO> getProfessor(@PathVariable UUID id) {
        Optional<Professor> studentById = service.getProfessorById(id);
        return studentById.map(student ->
                        ResponseEntity.ok(new ProfessorDataDTO(student)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveProfessor(@Valid @RequestBody NewProfessorDTO newProfessorDTO) {
        Optional<Professor> professorByEmail = service.getProfessorByEmail(newProfessorDTO.email());

        if (professorByEmail.isPresent()) return ResponseEntity.badRequest().body("This email address is already registered");

        Professor newProfessor = service.save(newProfessorDTO);
        return ResponseEntity.ok(new ProfessorDataDTO(newProfessor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        if (!service.existsById(id)) return ResponseEntity.notFound().build();

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
