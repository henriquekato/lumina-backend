package com.luminabackend.services;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.UserSignupDTO;
import com.luminabackend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username);
    }

    public User save(UserSignupDTO userSignupDTO){
        String username = userSignupDTO.username().trim();
        String email = userSignupDTO.email().trim();
        String password = userSignupDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(username, email, encodedPassword);
        return repository.save(user);
    }

    public Optional<User> getUserByEmail(String email){
        return repository.findByEmail(email);
    }
}
