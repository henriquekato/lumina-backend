package com.luminabackend.models.student;

import com.luminabackend.dtos.student.StudentDTO;
import com.luminabackend.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "students")
@Getter @Setter
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

    public Student(StudentDTO studentDTO){
        this.id = UUID.randomUUID();
        this.name = studentDTO.name();
        this.email = studentDTO.email();
        this.password = Utils.hashPassword(studentDTO.password());
    }
}
