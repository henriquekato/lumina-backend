package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteLastAdministratorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Admin;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminService {
    @Autowired
    private AdminRepository repository;

    @Autowired
    private UserService userService;

    public Page<Admin> getPaginatedAdmins(Pageable page){
        return repository.findAll(page);
    }

    public Optional<Admin> getAdminById(UUID id) {
        return repository.findById(id);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Admin save(UserSignupDTO adminPostDTO){
        userService.validateUserSignupData(adminPostDTO);

        Optional<User> userByEmail = userService.getUserByEmail(adminPostDTO.email());
        if (userByEmail.isPresent())
            throw new EmailAlreadyInUseException();

        UserNewDataDTO userNewDataDTO = userService.prepareUserDataToSave(adminPostDTO);

        Admin admin = new Admin(userNewDataDTO);
        return repository.save(admin);
    }

    public Admin edit(UUID id, UserPutDTO userPutDTO){
        Optional<Admin> adminById = getAdminById(id);
        if(adminById.isEmpty())
            throw new EntityNotFoundException("Admin not found");

        Admin admin = adminById.get();
        admin = (Admin) userService.editUserData(admin, userPutDTO);
        return repository.save(admin);
    }

    public void deleteById(UUID id) {
        if (!existsById(id))
            throw new EntityNotFoundException("Admin not found");

        if (count() == 1)
            throw new CannotDeleteLastAdministratorException("The last administrator cannot be deleted");

        repository.deleteById(id);
    }

    long count(){
        return repository.count();
    }
}
