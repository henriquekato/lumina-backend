package com.luminabackend.controllers;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserLoginDTO;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.security.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AccountController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDTO data){
        String email = data.email().trim();
        String password = data.password().trim();

        var token = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = manager.authenticate(token);
        TokenDTO tokenData = new TokenDTO(tokenService.generateToken((User) authentication.getPrincipal()));
        return ResponseEntity.ok(tokenData);
    }
}
