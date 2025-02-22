package com.luminabackend.services;

import com.luminabackend.models.user.User;
import com.luminabackend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = repository.findByEmail(email);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("Email not found");
        }
        return user.get();
    }

    public Optional<User> getUserByEmail(String email){
        return repository.findByEmail(email);
    }
}
