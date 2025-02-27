package com.luminabackend.utils.security;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luminabackend.exceptions.AuthorizationHeaderNotFoundException;
import com.luminabackend.models.user.User;
import com.luminabackend.repositories.user.UserRepository;
import com.luminabackend.services.TokenService;
import com.luminabackend.utils.errors.GeneralErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            if (Objects.equals(requestURI, "/login")) {
                filterChain.doFilter(request, response);
                return;
            }

            var authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null)
                throw new AuthorizationHeaderNotFoundException("Required Authorization header was not provided. Please include a valid Bearer token");

            String token = authorizationHeader.replace("Bearer ", "");
            var subject = tokenService.getSubject(token);

            Optional<User> optionalUser = repository.findByEmail(subject);
            if (optionalUser.isEmpty()) {
                throw new UsernameNotFoundException("Incorrect username or password");
            }

            User user = optionalUser.get();
            var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AuthorizationHeaderNotFoundException | UsernameNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String errorJson = objectMapper.writeValueAsString(new GeneralErrorResponseDTO(e.getMessage()));
                writer.write(errorJson);
            }
        } catch (JWTVerificationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String errorJson = objectMapper.writeValueAsString(new GeneralErrorResponseDTO("Invalid or expired JWT token"));
                writer.write(errorJson);
            }
        } catch (JWTCreationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try (PrintWriter writer = response.getWriter()) {
                ObjectMapper objectMapper = new ObjectMapper();
                String errorJson = objectMapper.writeValueAsString(new GeneralErrorResponseDTO("Error generating JWT token"));
                writer.write(errorJson);
            }
        }
    }
}
