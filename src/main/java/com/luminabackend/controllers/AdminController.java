package com.luminabackend.controllers;

import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.admin.AdminGetDTO;
import com.luminabackend.models.user.dto.admin.AdminPostDTO;
import com.luminabackend.services.AccountService;
import com.luminabackend.services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
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
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveAdmin(@Valid @RequestBody AdminPostDTO adminPostDTO, UriComponentsBuilder uriBuilder) {
        Optional<User> adminByEmail = accountService.getUserByEmail(adminPostDTO.email());

        if (adminByEmail.isPresent()) return ResponseEntity.badRequest().body("This email address is already registered");

        Admin newAdmin = service.save(adminPostDTO);
        var uri = uriBuilder.path("/admin/{id}").buildAndExpand(newAdmin.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable UUID id) {
        if (!service.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        if (service.count() == 1) {
            return ResponseEntity.badRequest().body("The last administrator cannot be deleted");
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
