package com.luminabackend.controllers.professor;

import com.luminabackend.models.education.task.TaskFullGetDTO;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserGetDTO;
import com.luminabackend.services.ProfessorService;
import com.luminabackend.services.TokenService;
import com.luminabackend.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    @GetMapping
    public ResponseEntity<Page<UserGetDTO>> getPaginatedProfessors(Pageable page) {
        Page<User> admins = service.getPaginatedUsers(List.of(Role.PROFESSOR), page);
        return ResponseEntity.ok(admins.map(UserGetDTO::new));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessor(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskFullGetDTO>> getProfessorTasks(
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Page<TaskFullGetDTO> list = service.getProfessorTasks(payloadDTO.id(), page).map(TaskFullGetDTO::new);
        return ResponseEntity.ok(list);
    }
}
