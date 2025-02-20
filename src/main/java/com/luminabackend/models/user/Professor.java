package com.luminabackend.models.user;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.professor.ProfessorPostDTO;
import com.luminabackend.utils.security.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "professors")
@Getter
@Setter
@NoArgsConstructor
public final class Professor extends User {
    private String firstName;
    private String lastName;

    public Professor(String username, String email, String password, String firstName, String lastName) {
        super(username, email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Professor(ProfessorPostDTO professorPostDTO){
        super(professorPostDTO.username(), professorPostDTO.email(), professorPostDTO.password());
        this.firstName = professorPostDTO.firstName();
        this.lastName = professorPostDTO.lastName();
    }
}
