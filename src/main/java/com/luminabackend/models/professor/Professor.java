package com.luminabackend.models.professor;

import com.luminabackend.dtos.professor.NewProfessorDTO;
import com.luminabackend.utils.Utils;
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

    public Professor(NewProfessorDTO newProfessorDTO) {
        this.id = UUID.randomUUID();
        this.name = newProfessorDTO.name();
        this.email = newProfessorDTO.email();
        this.password = Utils.hashPassword(newProfessorDTO.password());
    }
}
