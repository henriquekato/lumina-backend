package com.luminabackend.services;

import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.dto.admin.AdminPostDTO;
import com.luminabackend.repositories.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {
    @Autowired
    private AdminRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin save(AdminPostDTO adminPostDTO){
        String email = adminPostDTO.email().trim();
        String password = adminPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);

        Admin admin = new Admin(email, encodedPassword);
        return repository.save(admin);
    }

    public List<Admin> getAllAdmins() {
        return repository.findAll();
    }

    public Optional<Admin> getAdminById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Admin> getAdminByEmail(String email){
        return repository.findByEmail(email);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public long count(){
        return repository.count();
    }
}
