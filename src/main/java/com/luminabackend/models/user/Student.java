package com.luminabackend.models.user;

import com.luminabackend.models.user.dto.student.StudentPostDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Document(collection = "students")
@Getter
@Setter
@NoArgsConstructor
public final class Student extends User {
    private String firstName;
    private String lastName;

    public Student(String email, String password, String firstName, String lastName) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(StudentPostDTO studentPostDTO){
        super(studentPostDTO.email(), studentPostDTO.password());
        this.firstName = studentPostDTO.firstName();
        this.lastName = studentPostDTO.lastName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }
}
