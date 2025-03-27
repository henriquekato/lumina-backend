package com.luminabackend.models.user;

import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Document(collection = "admin")
@Getter
@Setter
@NoArgsConstructor
public final class Admin extends User {
    public Admin(UUID id, String email, String password, String firstName, String lastName) {
        super(id, email, password, firstName, lastName);
    }

    public Admin(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }

    public Admin(UserSignupDTO adminPostDTO){
        super(adminPostDTO.email(), adminPostDTO.password(), adminPostDTO.firstName(), adminPostDTO.lastName());
    }

    public Admin(UserNewDataDTO adminNewDataDTO){
        super(adminNewDataDTO.email(), adminNewDataDTO.password(), adminNewDataDTO.firstName(), adminNewDataDTO.lastName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
