package com.luminabackend.controllers;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.professor.ProfessorPostDTO;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.services.AccountService;
import com.luminabackend.services.ProfessorService;
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
@RequestMapping("/professor")
public class ProfessorController {
    @Autowired
    private ProfessorService service;

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<ProfessorGetDTO>> getAllProfessors() {
        List<Professor> professors = service.getAllProfessors();
        return professors.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(professors.stream().map(ProfessorGetDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> getProfessor(@PathVariable UUID id) {
        Optional<Professor> professorById = service.getProfessorById(id);
        return professorById.map(professor ->
                        ResponseEntity.ok(new ProfessorGetDTO(professor)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveProfessor(@Valid @RequestBody ProfessorPostDTO professorPostDTO, UriComponentsBuilder uriBuilder) {
        Optional<User> professorByEmail = accountService.getUserByEmail(professorPostDTO.email());

        if (professorByEmail.isPresent()) return ResponseEntity.badRequest().body("This email address is already registered");

        Professor newProfessor = service.save(professorPostDTO);
        var uri = uriBuilder.path("/professor/{id}").buildAndExpand(newProfessor.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        if (!service.existsById(id)) return ResponseEntity.notFound().build();

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
