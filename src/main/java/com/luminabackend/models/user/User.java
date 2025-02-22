package com.luminabackend.models.user;

import com.luminabackend.models.user.dto.user.UserSignupDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

@Document(collection = "users")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public sealed abstract class User implements UserDetails permits Student, Professor, Admin  {
    @Id
    private UUID id;
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(UserSignupDTO userSignupDTO) {
        this.id = UUID.randomUUID();
        this.username = userSignupDTO.username();
        this.email = userSignupDTO.email();
        this.password = userSignupDTO.password();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
