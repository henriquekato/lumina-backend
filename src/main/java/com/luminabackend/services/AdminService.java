package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteLastAdministratorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Admin> getAllAdmins() {
        return repository.findAll();
    }

    public Page<Admin> getPaginatedAdmins(Pageable page){
        return repository.findAll(page);
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

    public Admin save(UserSignupDTO adminPostDTO){
        Optional<User> userByEmail = accountService.getUserByEmail(adminPostDTO.email());

        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        String email = adminPostDTO.email().trim();
        String password = adminPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = adminPostDTO.firstName().trim();
        String lastName = adminPostDTO.lastName().trim();

        Admin admin = new Admin(email, encodedPassword, firstName, lastName);
        return repository.save(admin);
    }

    public Admin edit(UUID id, UserPutDTO userPutDTO){
        Optional<Admin> adminById = getAdminById(id);
        if(adminById.isEmpty()) throw new EntityNotFoundException("Admin not found");

        Admin admin = adminById.get();
        admin = (Admin) accountService.editUserData(admin, userPutDTO);
        return repository.save(admin);
    }

    public void deleteById(UUID id) {
        if (!existsById(id)) throw new EntityNotFoundException("Admin not found");

        if (count() == 1) {
            throw new CannotDeleteLastAdministratorException("The last administrator cannot be deleted");
        }

        repository.deleteById(id);
    }

    public long count(){
        return repository.count();
    }
}
