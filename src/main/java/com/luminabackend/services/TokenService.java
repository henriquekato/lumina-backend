package com.luminabackend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class TokenService {
    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.issuer}")
    private String issuer;

    public String generateToken(User user){
        var algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getUsername())
                .withExpiresAt(expirationDate())
                .withClaim("id", user.getId().toString())
                .withClaim("first_name", user.getFirstName())
                .withClaim("last_name", user.getLastName())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getClass().getSimpleName().toLowerCase())
                .sign(algorithm);
    }

    public String getSubject(String tokenJWT){
        var algorithm = Algorithm.HMAC256(secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return jwtVerifier.verify(tokenJWT).getSubject();
    }

    public PayloadDTO getPayloadFromAuthorizationHeader(String authorizationHeader){
        var algorithm = Algorithm.HMAC256(secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        DecodedJWT decodedJWT = jwtVerifier.verify(authorizationHeader.replace("Bearer ", ""));
        UUID id = UUID.fromString(decodedJWT.getClaim("id").asString());
        String firstName = decodedJWT.getClaim("first_name").asString();
        String lastName = decodedJWT.getClaim("last_name").asString();
        String email = decodedJWT.getClaim("email").asString();
        Role role = Role.getRoleFromString(decodedJWT.getClaim("role").asString());
        return new PayloadDTO(id, firstName, lastName, email, role);
    }

    private Instant expirationDate(){
        return LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("-03:00"));
    }
}
