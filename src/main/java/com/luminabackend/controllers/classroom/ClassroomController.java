package com.luminabackend.controllers.classroom;

import com.luminabackend.models.education.classroom.*;
import com.luminabackend.models.user.dto.UserAccessDTO;
import com.luminabackend.services.*;
import com.luminabackend.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/classroom")
public class ClassroomController implements ClassroomControllerDocumentation {
    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccessService accessService;

    @Autowired
    private ClassroomWithRelationsService classroomWithRelationsService;

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping
    public ResponseEntity<Page<ClassroomGetDTO>> getPaginatedClassrooms(
            Pageable page,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Page<Classroom> classrooms = classroomService.getPaginatedClassroomsBasedOnUserAccess(new UserAccessDTO(payloadDTO), page);
        return ResponseEntity.ok(classrooms.map(ClassroomGetDTO::new));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}")
    public ResponseEntity<ClassroomGetDTO> getClassroom(
            @PathVariable UUID classroomId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        accessService.checkAccessToClassroomById(classroomId, new UserAccessDTO(payloadDTO));
        Classroom classroom = classroomService.getClassroomById(classroomId);
        return ResponseEntity.ok(new ClassroomGetDTO(classroom));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or hasRole('STUDENT')")
    @GetMapping("/{classroomId}/members")
    public ResponseEntity<ClassroomWithRelationsDTO> getClassroomWithRelations(
        @PathVariable UUID classroomId,
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        Classroom classroom = classroomService.getClassroomById(classroomId);
        accessService.checkAccessToClassroom(classroom, new UserAccessDTO(payloadDTO));
        ClassroomWithRelationsDTO fullClassroom = classroomWithRelationsService.getClassroomWithRelations(classroom);
        return ResponseEntity.ok(fullClassroom);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassroomGetDTO> saveClassroom(
            @Valid @RequestBody ClassroomPostDTO classroomPostDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        Classroom savedClassroom = classroomService.save(classroomPostDTO);
        return ResponseEntity
                .created(linkTo(methodOn(ClassroomController.class)
                        .getClassroom(savedClassroom.getId(), authorizationHeader))
                        .toUri())
                .body(new ClassroomGetDTO(savedClassroom));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') and @professorExistence.verify(#classroomPutDTO)")
    @PutMapping("/{classroomId}")
    public ResponseEntity<ClassroomGetDTO> editClassroom(
            @PathVariable UUID classroomId,
            @Valid @RequestBody ClassroomPutDTO classroomPutDTO
    ) {
        Classroom classroom = classroomService.edit(classroomId, classroomPutDTO);
        return ResponseEntity.ok(new ClassroomGetDTO(classroom));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{classroomId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable UUID classroomId) {
        classroomService.deleteById(classroomId);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @PostMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Void> addStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        accessService.checkAccessToClassroomById(classroomId, new UserAccessDTO(payloadDTO));
        Classroom classroom = classroomService.getClassroomById(classroomId);
        classroomService.addStudentToClassroom(studentId, classroom);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
    @DeleteMapping("/{classroomId}/student/{studentId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable UUID classroomId,
            @PathVariable UUID studentId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        accessService.checkAccessToClassroomById(classroomId, new UserAccessDTO(payloadDTO));
        Classroom classroom = classroomService.getClassroomById(classroomId);
        classroomService.removeStudentFromClassroom(studentId, classroom);
        return ResponseEntity.noContent().build();
    }
}
