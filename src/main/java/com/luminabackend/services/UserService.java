package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> getUserByEmail(String email){
        return repository.findByEmail(email);
    }

    public Optional<User> getUserById(UUID id){
        return repository.findByUUID(id);
    }

    public void validateUserSignupData(UserSignupDTO userSignupDTO){
        if (userSignupDTO == null) throw new IllegalArgumentException("User signup DTO is null");
        if (userSignupDTO.email() == null || userSignupDTO.email().isBlank()) throw new IllegalArgumentException("Email is null or blank");
        if (userSignupDTO.password() == null || userSignupDTO.password().isBlank()) throw new IllegalArgumentException("Password is null or blank");
        if (userSignupDTO.firstName() == null || userSignupDTO.firstName().isBlank()) throw new IllegalArgumentException("First name is null or blank");
        if (userSignupDTO.lastName() == null || userSignupDTO.lastName().isBlank()) throw new IllegalArgumentException("Last name is null or blank");
    }

    public UserNewDataDTO prepareUserDataToSave(UserSignupDTO userSignupDTO){
        String email = userSignupDTO.email().trim();
        String password = userSignupDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = userSignupDTO.firstName().trim();
        String lastName = userSignupDTO.lastName().trim();
        return new UserNewDataDTO(email, encodedPassword, firstName, lastName);
    }

    public User editUserData(User user, UserPutDTO userPutDTO){
        String newEmail = userPutDTO.email();
        if (newEmail != null) {
            newEmail = newEmail.trim();
            Optional<User> userByEmail = getUserByEmail(newEmail);
            if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();
            user.setEmail(newEmail);
        }
        if (userPutDTO.password() != null) {
            user.setPassword(userPutDTO.password().trim());
        }
        if (userPutDTO.firstName() != null) {
            user.setFirstName(userPutDTO.firstName().trim());
        }
        if (userPutDTO.lastName() != null) {
            user.setLastName(userPutDTO.lastName().trim());
        }
        return user;
    }
}
