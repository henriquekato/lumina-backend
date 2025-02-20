package com.luminabackend.controllers;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.UserLoginDTO;
import com.luminabackend.models.user.UserSignupDTO;
import com.luminabackend.services.AccountService;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.errors.ErrorResponseDTO;
import com.luminabackend.utils.security.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping
public class AccountController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO data){
        String username = data.username().trim();
        String password = data.password().trim();

        try {
            var token = new UsernamePasswordAuthenticationToken(username, password);
            var authentication = manager.authenticate(token);
            TokenDTO tokenData = new TokenDTO(tokenService.generateToken((User) authentication.getPrincipal()));
            return ResponseEntity.ok(tokenData);
        } catch (UsernameNotFoundException e){
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("auth", "Incorrect username or password"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserSignupDTO userSignupDTO, UriComponentsBuilder uriBuilder){
        Optional<User> userByEmail = accountService.getUserByEmail(userSignupDTO.email());

        if (userByEmail.isPresent()) return ResponseEntity.badRequest().body("This email address is already registered");

        User newUser = accountService.save(userSignupDTO);

        var uri = uriBuilder.path("/user/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
}
