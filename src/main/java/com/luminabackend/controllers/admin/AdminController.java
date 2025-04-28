package com.luminabackend.controllers.admin;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskFullGetDTO;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.admin.AdminGetDTO;
import com.luminabackend.models.user.dto.user.UserGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.AdminService;
import com.luminabackend.services.TaskService;
import com.luminabackend.services.UserService;
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
@RequestMapping("/admin")
public class AdminController implements AdminControllerDocumentation {
    @Autowired
    private AdminService service;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

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
    public ResponseEntity<Page<UserGetDTO>> getAllUsers(@RequestParam(required = false) List<String> role, Pageable page) {
        List<Role> roles = Role.parseRoles(role);
        Page<UserGetDTO> users = userService.getPaginatedUsers(roles, page).map(UserGetDTO::new);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskFullGetDTO>> getAllTasks(Pageable page) {
        Page<Task> tasks = taskService.getAllTasks(page);
        return ResponseEntity.ok(tasks.map(TaskFullGetDTO::new));
    }
}
