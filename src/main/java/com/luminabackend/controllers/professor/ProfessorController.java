package com.luminabackend.controllers.professor;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.task.TaskFullGetDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.ProfessorService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/professor")
public class ProfessorController implements ProfessorControllerDocumentation {
    @Autowired
    private ProfessorService service;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR') or hasRole('ADMIN')")
    @Override
    @GetMapping("/all")
    public ResponseEntity<List<ProfessorGetDTO>> getAllProfessors() {
        List<Professor> professors = service.getAllProfessors();
        return ResponseEntity.ok(professors.stream().map(ProfessorGetDTO::new).toList());
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @Override
    @GetMapping
    public ResponseEntity<Page<ProfessorGetDTO>> getPaginatedProfessors(Pageable page) {
        Page<Professor> professors = service.getPaginatedProfessors(page);
        return ResponseEntity.ok(professors.map(ProfessorGetDTO::new));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> getProfessor(@PathVariable UUID id) {
        Optional<Professor> professorById = service.getProfessorById(id);
        return professorById.map(professor ->
                        ResponseEntity.ok(new ProfessorGetDTO(professor)))
                .orElseThrow(() -> new EntityNotFoundException(("Professor not found")));
    }

    @Override
    @PostMapping
    public ResponseEntity<ProfessorGetDTO> saveProfessor(@Valid @RequestBody UserSignupDTO professorPostDTO) {
        Professor newProfessor = service.save(professorPostDTO);
        return ResponseEntity
                .created(linkTo(methodOn(ProfessorController.class)
                        .getProfessor(newProfessor.getId()))
                        .toUri())
                .body(new ProfessorGetDTO(newProfessor));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ProfessorGetDTO> editProfessor(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO) {
        Professor professor = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new ProfessorGetDTO(professor));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROFESSOR') or hasRole('ADMIN')")
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskFullGetDTO>> getProfessorTasks(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        List<TaskFullGetDTO> list = service.getProfessorTasks(payloadDTO.id()).stream().map(TaskFullGetDTO::new).toList();
        return ResponseEntity.ok(list);
    }
}
