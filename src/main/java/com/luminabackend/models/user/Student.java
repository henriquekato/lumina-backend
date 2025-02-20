package com.luminabackend.models.user;

import com.luminabackend.models.user.User;
import com.luminabackend.models.user.student.StudentPostDTO;
import com.luminabackend.utils.security.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "students")
@Getter
@Setter
@NoArgsConstructor
public final class Student extends User {
    private String firstName;
    private String lastName;

    public Student(String username, String email, String password, String firstName, String lastName) {
        super(username, email, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(StudentPostDTO studentPostDTO){
        super(studentPostDTO.username(), studentPostDTO.email(), studentPostDTO.password());
        this.firstName = studentPostDTO.firstName();
        this.lastName = studentPostDTO.lastName();
    }
}
