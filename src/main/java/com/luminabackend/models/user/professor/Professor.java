package com.luminabackend.models.user.professor;

import com.luminabackend.utils.security.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "professors")
@Getter
@Setter
public class Professor {
    @Id
    private UUID id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String password;

    public Professor() {
        this.id = UUID.randomUUID();
    }

    public Professor(ProfessorPostDTO professorPostDTO) {
        this.id = UUID.randomUUID();
        this.name = professorPostDTO.name();
        this.email = professorPostDTO.email();
        this.password = Utils.hashPassword(professorPostDTO.password());
    }
}
