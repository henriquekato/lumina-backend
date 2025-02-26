package com.luminabackend.controllers;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.admin.AdminGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.AccountService;
import com.luminabackend.services.AdminService;
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
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService service;

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AdminGetDTO>> getAllAdmins() {
        List<Admin> admins = service.getAllAdmins();
        return admins.isEmpty() ?
                ResponseEntity.noContent().build()
                : ResponseEntity.ok(admins.stream().map(AdminGetDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminGetDTO> getAdmin(@PathVariable UUID id) {
        Optional<Admin> adminById = service.getAdminById(id);
        return adminById.map(admin ->
                ResponseEntity.ok(new AdminGetDTO(admin)))
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
    }

    @PostMapping
    public ResponseEntity<?> saveAdmin(@Valid @RequestBody UserSignupDTO adminPostDTO, UriComponentsBuilder uriBuilder) {
        Optional<User> userByEmail = accountService.getUserByEmail(adminPostDTO.email());

        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        Admin newAdmin = service.save(adminPostDTO);
        var uri = uriBuilder.path("/admin/{id}").buildAndExpand(newAdmin.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editAdmin(@PathVariable UUID id, @Valid @RequestBody UserPutDTO adminPostDTO) {
        Optional<Admin> adminById = service.getAdminById(id);
        if(adminById.isEmpty()) throw new EntityNotFoundException("Admin not found");

        Admin admin = adminById.get();
        String newEmail = adminPostDTO.email();
        if (newEmail != null) {
            newEmail = newEmail.trim();
            Optional<User> user = accountService.getUserByEmail(newEmail);
            if (user.isPresent()) throw new EmailAlreadyInUseException();
            admin.setEmail(newEmail);
        }
        if (adminPostDTO.password() != null) {
            admin.setPassword(adminPostDTO.password().trim());
        }
        if (adminPostDTO.firstName() != null) {
            admin.setFirstName(adminPostDTO.firstName().trim());
        }
        if (adminPostDTO.lastName() != null) {
            admin.setLastName(adminPostDTO.lastName().trim());
        }

        service.save(admin);
        return ResponseEntity.ok(admin);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable UUID id) {
        if (!service.existsById(id)) throw new EntityNotFoundException("Admin not found");

        if (service.count() == 1) {
            return ResponseEntity.badRequest().body("The last administrator cannot be deleted");
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
