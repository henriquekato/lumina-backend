package com.luminabackend.utils.security;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luminabackend.repositories.UserRepository;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.errors.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authorizationHeader = request.getHeader("Authorization");

        try {
            if (authorizationHeader != null) {
                String token = authorizationHeader.replace("Bearer ", "");
                var subject = tokenService.getSubject(token);
                var user = repository.findByUsername(subject);
                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String errorJson = objectMapper.writeValueAsString(new ErrorResponseDTO("JWT", "Invalid or expired JWT token"));
                writer.write(errorJson);
            }
        } catch (JWTCreationException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String errorJson = objectMapper.writeValueAsString(new ErrorResponseDTO("JWT", "Error generating JWT token"));
                writer.write(errorJson);
            }
        }
    }
}
