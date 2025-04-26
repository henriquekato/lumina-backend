package com.luminabackend.controllers.admin;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.task.TaskFullGetDTO;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Student;
import com.luminabackend.models.user.dto.admin.AdminGetDTO;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.models.user.dto.user.UserGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.*;
import com.luminabackend.utils.security.PayloadDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController implements AdminControllerDocumentation {
    @Autowired
    private AdminService service;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private TokenService tokenService;

    @Override
    @GetMapping("/all")
    public ResponseEntity<List<AdminGetDTO>> getAllAdmins() {
        List<Admin> admins = service.getAllAdmins();
        return ResponseEntity.ok(admins.stream().map(AdminGetDTO::new).toList());
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<AdminGetDTO>> getPaginatedAdmins(Pageable page) {
        Page<Admin> admins = service.getPaginatedAdmins(page);
        return ResponseEntity.ok(admins.map(AdminGetDTO::new));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<AdminGetDTO> getAdmin(@PathVariable UUID id) {
        Optional<Admin> adminById = service.getAdminById(id);
        return adminById.map(admin ->
                        ResponseEntity.ok(new AdminGetDTO(admin)))
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
    }

    @Override
    @PostMapping
    public ResponseEntity<AdminGetDTO> saveAdmin(@Valid @RequestBody UserSignupDTO adminPostDTO) {
        Admin newAdmin = service.save(adminPostDTO);
        return ResponseEntity
                .created(linkTo(methodOn(AdminController.class)
                        .getAdmin(newAdmin.getId()))
                        .toUri())
                .body(new AdminGetDTO(newAdmin));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<AdminGetDTO> editAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO) {
        Admin admin = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new AdminGetDTO(admin));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserGetDTO>> getAllUsers(@RequestParam List<String> role) {
        List<UserGetDTO> users = new ArrayList<>();
        if (role.contains("student")) {
            List<Student> students = studentService.getAllStudents();
            users.addAll(students.stream().map(student -> new UserGetDTO(student, "student")).toList());
        }
        if (role.contains("professor")) {
            List<Professor> professors = professorService.getAllProfessors();
            users.addAll(professors.stream().map(professor -> new UserGetDTO(professor, "professor")).toList());
        }
        if (role.contains("admin")){
            List<Admin> admins = service.getAllAdmins();
            users.addAll(admins.stream().map(admin -> new UserGetDTO(admin, "admin")).toList());
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskFullGetDTO>> getAllTasks(@RequestHeader("Authorization") String authorizationHeader) {
        PayloadDTO payloadDTO = tokenService.getPayloadFromAuthorizationHeader(authorizationHeader);
        List<Classroom> classrooms = classroomService.getClassroomsBasedOnUserAccess(new UserAccessDTO(payloadDTO));
        List<TaskFullGetDTO> tasks = taskService.getAllTasks()
                .stream().map(task -> {
                    Optional<Classroom> classroom =
                            classrooms.stream()
                                    .filter(c -> c.getId().equals(task.getClassroomId()))
                                    .findAny();
                    classroom.ifPresentOrElse(
                            c->task.setClassroomName(c.getName()),
                            ()->{throw new IllegalStateException("Error");});
                    return task;
                }).map(TaskFullGetDTO::new)
                .toList();
        return ResponseEntity.ok(tasks);
    }
}
