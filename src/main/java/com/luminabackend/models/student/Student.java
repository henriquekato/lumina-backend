package com.luminabackend.models.student;

import com.luminabackend.dtos.student.NewStudentDTO;
import com.luminabackend.utils.Utils;
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

    public Student(NewStudentDTO newStudentDTO){
        this.id = UUID.randomUUID();
        this.name = newStudentDTO.name();
        this.email = newStudentDTO.email();
        this.password = Utils.hashPassword(newStudentDTO.password());
    }
}
