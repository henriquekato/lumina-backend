package com.luminabackend.controllers;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.dto.admin.AdminGetDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService service;

    @GetMapping
    public ResponseEntity<List<AdminGetDTO>> getAllAdmins() {
        List<Admin> admins = service.getAllAdmins();
        return ResponseEntity.ok(admins.stream().map(AdminGetDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminGetDTO> getAdmin(@PathVariable UUID id) {
        Optional<Admin> adminById = service.getAdminById(id);
        return adminById.map(admin ->
                ResponseEntity.ok(new AdminGetDTO(admin)))
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
    }

    @PostMapping
    public ResponseEntity<AdminGetDTO> saveAdmin(@Valid @RequestBody UserSignupDTO adminPostDTO) {
        Admin newAdmin = service.save(adminPostDTO);
        return ResponseEntity.ok(new AdminGetDTO(newAdmin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminGetDTO> editAdmin(@PathVariable UUID id, @Valid @RequestBody UserPutDTO userPutDTO) {
        Admin admin = service.edit(id, userPutDTO);
        return ResponseEntity.ok(new AdminGetDTO(admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
