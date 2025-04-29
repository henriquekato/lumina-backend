package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteLastAdministratorException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.SuperUserAlreadyCreated;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserSignupDTO;
import com.luminabackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminService extends UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private TaskService taskService;

    public Page<Task> getAllTasks(Pageable page){
        return taskService.getAllTasks(page);
    }

    public User createSuperUser(){
        if (repository.countUserByRoleIs(Role.ADMIN) > 0) throw new SuperUserAlreadyCreated("Super user already created");
        String email = "superuser@email.com";
        String password = "superuser";
        String firstName = "user";
        String lastName = "user";
        Role role = Role.ADMIN;
        UserSignupDTO userSignupDTO = new UserSignupDTO(email, password, firstName, lastName, role.toString());
        return save(userSignupDTO);
    }
}
