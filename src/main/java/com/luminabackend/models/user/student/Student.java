package com.luminabackend.models.user.student;

import com.luminabackend.utils.security.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "students")
@Getter
@Setter
public class Student {
    @Id
    private UUID id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String password;

    public Student(){
        this.id = UUID.randomUUID();
    }

    public Student(StudentPostDTO studentPostDTO){
        this.id = UUID.randomUUID();
        this.name = studentPostDTO.name();
        this.email = studentPostDTO.email();
        this.password = Utils.hashPassword(studentPostDTO.password());
    }
}
