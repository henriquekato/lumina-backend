package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserNewDataDTO;
import com.luminabackend.models.user.dto.UserPutDTO;
import com.luminabackend.models.user.dto.UserSignupDTO;
import com.luminabackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserValidatorService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

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
        Role role = Role.getRoleFromString(userSignupDTO.role());
        return new UserNewDataDTO(email, encodedPassword, firstName, lastName, role);
    }

    public User editUserData(User user, UserPutDTO userPutDTO){
        if (userPutDTO.email() != null && !userPutDTO.email().isBlank() && !Objects.equals(user.getEmail(), userPutDTO.email())) {
            String newEmail = userPutDTO.email().trim();
            Optional<User> userByEmail = repository.findByEmail(newEmail);
            if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();
            user.setEmail(newEmail);
        }
        if (userPutDTO.password() != null && !userPutDTO.password().isBlank()) {
            String password = userPutDTO.password().trim();
            user.setPassword(passwordEncoder.encode(password));
        }
        if (userPutDTO.firstName() != null && !userPutDTO.firstName().isBlank()) {
            user.setFirstName(userPutDTO.firstName().trim());
        }
        if (userPutDTO.lastName() != null && !userPutDTO.lastName().isBlank()) {
            user.setLastName(userPutDTO.lastName().trim());
        }
        return user;
    }
}
