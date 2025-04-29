package com.luminabackend.controllers.user;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserGetDTO;
import com.luminabackend.models.user.dto.UserPutDTO;
import com.luminabackend.models.user.dto.UserSignupDTO;
import com.luminabackend.services.AdminService;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AdminService service;

    @GetMapping
    public ResponseEntity<Page<UserGetDTO>> getUsers(@RequestParam(required = false) List<String> role, Pageable page) {
        List<Role> roles = Role.parseRoles(role);
        Page<UserGetDTO> users = service.getPaginatedUsers(roles, page).map(UserGetDTO::new);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserGetDTO> getUser(@PathVariable UUID id) {
        Optional<User> userById = service.getUserById(id);
        return userById.map(user ->
                        ResponseEntity.ok(new UserGetDTO(user)))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @PostMapping
    public ResponseEntity<UserGetDTO> saveUser(@Valid @RequestBody UserSignupDTO userSignupDTO) {
        User newUser = service.save(userSignupDTO);
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class)
                        .getUser(newUser.getId()))
                        .toUri())
                .body(new UserGetDTO(newUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGetDTO> editUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserPutDTO userPutDTO) {
        User user = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new UserGetDTO(user));
    }
}
