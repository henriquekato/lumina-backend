package com.luminabackend.models.user;

import com.luminabackend.models.user.dto.admin.AdminPostDTO;
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
    public Admin(String email, String password) {
        super(email, password);
    }

    public Admin(AdminPostDTO adminPostDTO){
        super(adminPostDTO.email(), adminPostDTO.password());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
}
