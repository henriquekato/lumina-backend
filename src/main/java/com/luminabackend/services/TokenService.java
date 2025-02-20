package com.luminabackend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.luminabackend.models.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
                .sign(algorithm);
    }

    public String getSubject(String tokenJWT){
        var algorithm = Algorithm.HMAC256(secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return jwtVerifier.verify(tokenJWT).getSubject();
    }

    private Instant expirationDate(){
        return LocalDateTime.now().plusDays(3).toInstant(ZoneOffset.of("-03:00"));
    }
}
