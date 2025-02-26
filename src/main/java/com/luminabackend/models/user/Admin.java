package com.luminabackend.models.user;

import com.luminabackend.models.user.dto.user.UserSignupDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Document(collection = "admin")
@Getter
@Setter
@NoArgsConstructor
public final class Admin extends User {
    public Admin(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }

    public Admin(UserSignupDTO adminPostDTO){
        super(adminPostDTO.email(), adminPostDTO.password(), adminPostDTO.firstName(), adminPostDTO.lastName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
