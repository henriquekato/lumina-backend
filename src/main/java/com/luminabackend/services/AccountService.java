package com.luminabackend.services;

import com.luminabackend.models.user.User;
import com.luminabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails byUsername = repository.findByUsername(username);
        if (byUsername == null){
            throw new UsernameNotFoundException("Username not found");
        }
        return byUsername;
    }

    public Optional<User> getUserByEmail(String email){
        return repository.findByEmail(email);
    }
}
