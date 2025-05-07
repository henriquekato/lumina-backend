package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteActiveProfessorException;
import com.luminabackend.exceptions.CannotDeleteLastAdministratorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserAccessDTO;
import com.luminabackend.models.user.dto.UserNewDataDTO;
import com.luminabackend.models.user.dto.UserPutDTO;
import com.luminabackend.models.user.dto.UserSignupDTO;
import com.luminabackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public abstract class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserValidatorService validatorService;

    @Autowired
    private ClassroomService classroomService;

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmail(email);
    }

    public Optional<User> getUserById(UUID id) {
        return repository.findById(id);
    }

    public List<User> getUsersById(List<UUID> ids){
        return repository.findAllById(ids);
    }

    public Page<User> getPaginatedUsers(List<Role> roles, Pageable page) {
        if (roles.isEmpty()) {
            roles = new ArrayList<>();
            roles.addAll(List.of(Role.ADMIN, Role.PROFESSOR, Role.STUDENT));
        }
        return repository.findAllByRoleIn(roles, page);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public User save(UserSignupDTO userPostDTO){
        validatorService.validateUserSignupData(userPostDTO);

        Optional<User> userByEmail = getUserByEmail(userPostDTO.email());
        if (userByEmail.isPresent())
            throw new EmailAlreadyInUseException();

        UserNewDataDTO userNewDataDTO = validatorService.prepareUserDataToSave(userPostDTO);

        User user = new User(userNewDataDTO);
        return repository.save(user);
    }

    public User edit(UUID id, UserPutDTO userPutDTO){
        Optional<User> userById = getUserById(id);
        if(userById.isEmpty())
            throw new EntityNotFoundException("User not found");

        User user = userById.get();
        user = validatorService.editUserData(user, userPutDTO);
        return repository.save(user);
    }

    @Transactional
    public void deleteById(UUID id) {
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isEmpty())
            throw new EntityNotFoundException("User not found");

        User user = userOptional.get();
        if (user.getRole() == Role.ADMIN){
            if (repository.countUserByRoleIs(Role.ADMIN) == 1)
                throw new CannotDeleteLastAdministratorException("The last administrator cannot be deleted");
        } else if (user.getRole() == Role.PROFESSOR) {
            if (!classroomService.getClassroomsBasedOnUserAccess(new UserAccessDTO(id, Role.PROFESSOR)).isEmpty())
                throw new CannotDeleteActiveProfessorException("Cannot delete professor because they are currently assigned to one or more active classrooms");
        } else {
            classroomService.removeStudentFromAllClassrooms(id);
        }

        repository.deleteById(id);
    }
}
