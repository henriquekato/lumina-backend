package com.luminabackend.controllers.student;

import com.luminabackend.models.education.task.TaskFullGetDTO;
import com.luminabackend.services.StudentService;
import com.luminabackend.services.TokenService;
import com.luminabackend.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/student")
public class StudentController implements StudentControllerDocumentation {
    @Autowired
    private StudentService service;

    @Autowired
    private TokenService tokenService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/tasks/done")
    public ResponseEntity<Page<TaskFullGetDTO>> getStudentDoneTasks(
            Pageable page, @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Page<TaskFullGetDTO> list = service.getStudentDoneTasks(payloadDTO.id(), page).map(TaskFullGetDTO::new);
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/tasks/not-done")
    public ResponseEntity<Page<TaskFullGetDTO>> getStudentNotDoneTasks(
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Page<TaskFullGetDTO> list = service.getStudentNotDoneTasks(payloadDTO.id(), page).map(TaskFullGetDTO::new);
        return ResponseEntity.ok(list);
    }
}
