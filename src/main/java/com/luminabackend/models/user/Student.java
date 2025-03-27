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

@Document(collection = "students")
@Getter
@Setter
@NoArgsConstructor
public final class Student extends User {
    public Student(UUID id, String email, String password, String firstName, String lastName) {
        super(id, email, password, firstName, lastName);
    }

    public Student(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }

    public Student(UserSignupDTO studentPostDTO){
        super(studentPostDTO.email(),
                studentPostDTO.password(),
                studentPostDTO.firstName(),
                studentPostDTO.lastName());
    }

    public Student(UserNewDataDTO studentNewDataDTO){
        super(studentNewDataDTO.email(),
                studentNewDataDTO.password(),
                studentNewDataDTO.firstName(),
                studentNewDataDTO.lastName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }
}
