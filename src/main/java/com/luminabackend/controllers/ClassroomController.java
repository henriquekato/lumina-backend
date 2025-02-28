package com.luminabackend.controllers;

import com.luminabackend.models.education.classroom.*;
import com.luminabackend.services.ClassroomService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/classroom")
public class ClassroomController {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<List<ClassroomGetDTO>> getAllClassrooms(@RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payload = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        List<Classroom> filteredClassrooms = classroomService.getFilteredClassrooms(payload.role(), payload.id());
        return ResponseEntity.ok(filteredClassrooms.stream().map(ClassroomGetDTO::new).toList());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}")
    public ResponseEntity<ClassroomResourceDTO> getClassroom(@PathVariable UUID classroomId,
                                                             @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.getClassroomBasedOnUserPermission(classroomId, payloadDTO);
        return ResponseEntity.ok(new ClassroomResourceDTO(classroom));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassroomResourceDTO> saveClassroom(@Valid @RequestBody
                                                   ClassroomPostDTO classroomPostDTO) {
        Classroom savedClassroom = classroomService.save(classroomPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ClassroomResourceDTO(savedClassroom));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClassroomResourceDTO> editClassroom(@PathVariable UUID id,
                                                              @Valid @RequestBody ClassroomPutDTO classroomPutDTO) {
        Classroom classroom = classroomService.edit(id, classroomPutDTO);
        return ResponseEntity.ok(new ClassroomResourceDTO(classroom));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable UUID classroomId) {
        classroomService.deleteById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<ClassroomResourceDTO> addStudent(@PathVariable UUID classroomId,
                                                           @PathVariable UUID studentId,
                                                           @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.addStudentToClassroom(classroomId, payloadDTO, studentId);
        return ResponseEntity.ok(new ClassroomResourceDTO(classroom));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Void> removeStudent(@PathVariable UUID classroomId,
                                              @PathVariable UUID studentId,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        classroomService.removeStudentFromClassroom(classroomId, payloadDTO, studentId);
        return ResponseEntity.noContent().build();
    }
}
