package com.luminabackend.controllers;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
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
                .orElseThrow(() -> new EntityNotFoundException(("Professor not found")));
    }

    @PostMapping
    public ResponseEntity<?> saveProfessor(@Valid @RequestBody UserSignupDTO professorPostDTO, UriComponentsBuilder uriBuilder) {
        Optional<User> userByEmail = accountService.getUserByEmail(professorPostDTO.email());

        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        Professor newProfessor = service.save(professorPostDTO);
        var uri = uriBuilder.path("/professor/{id}").buildAndExpand(newProfessor.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProfessor(@PathVariable UUID id, @Valid @RequestBody UserPutDTO professorPostDTO) {
        Optional<Professor> professorById = service.getProfessorById(id);
        if(professorById.isEmpty()) throw new EntityNotFoundException(("Professor not found"));

        Professor professor = professorById.get();
        String newEmail = professorPostDTO.email();
        if (newEmail != null) {
            newEmail = newEmail.trim();
            Optional<User> user = accountService.getUserByEmail(newEmail);
            if (user.isPresent()) throw new EmailAlreadyInUseException();
            professor.setEmail(newEmail);
        }
        if (professorPostDTO.password() != null) {
            professor.setPassword(professorPostDTO.password().trim());
        }
        if (professorPostDTO.firstName() != null) {
            professor.setFirstName(professorPostDTO.firstName().trim());
        }
        if (professorPostDTO.lastName() != null) {
            professor.setLastName(professorPostDTO.lastName().trim());
        }

        service.save(professor);
        return ResponseEntity.ok(professor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        if (!service.existsById(id)) throw new EntityNotFoundException(("Professor not found"));

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
