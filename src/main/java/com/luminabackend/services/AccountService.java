package com.luminabackend.services;

import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(email);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("Incorrect username or password");
        }
        return user.get();
    }

    public Optional<User> getUserByEmail(String email){
        return repository.findByEmail(email);
    }

    public Optional<User> getUserById(UUID id){
        return repository.findByUUID(id);
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
