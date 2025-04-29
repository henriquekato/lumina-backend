package com.luminabackend.controllers.account;

import com.luminabackend.controllers.user.UserController;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.UserGetDTO;
import com.luminabackend.models.user.dto.UserLoginDTO;
import com.luminabackend.services.AdminService;
import com.luminabackend.services.TokenService;
import com.luminabackend.security.TokenDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping
public class AccountController implements AccountControllerDocumentation {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminService service;

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody UserLoginDTO data){
        String email = data.email().trim();
        String password = data.password().trim();

        var token = new UsernamePasswordAuthenticationToken(email, password);
        var authentication = manager.authenticate(token);
        TokenDTO tokenData = new TokenDTO(tokenService.generateToken((User) authentication.getPrincipal()));
        return ResponseEntity.ok(tokenData);
    }

    @PostMapping("/create-super-user")
    public ResponseEntity<UserGetDTO> createSuperUser(){
        User superUser = service.createSuperUser();
        return ResponseEntity
                .created(linkTo(methodOn(UserController.class)
                        .getUser(superUser.getId()))
                        .toUri())
                .body(new UserGetDTO(superUser));
    }
}
