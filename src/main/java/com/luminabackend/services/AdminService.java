package com.luminabackend.services;

import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
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

    public Admin save(UserSignupDTO adminPostDTO){
        String email = adminPostDTO.email().trim();
        String password = adminPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = adminPostDTO.firstName().trim();
        String lastName = adminPostDTO.lastName().trim();

        Admin admin = new Admin(email, encodedPassword, firstName, lastName);
        return repository.save(admin);
    }

    public Admin save(Admin admin){
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
