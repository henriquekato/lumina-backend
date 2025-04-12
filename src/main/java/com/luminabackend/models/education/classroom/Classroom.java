package com.luminabackend.models.education.classroom;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "classrooms")
@Getter
@Setter
public class Classroom {
    @Id
    private UUID id;
    private String name;
    private String description;
    private UUID professorId;
    private List<UUID> studentsIds;

    public Classroom(ClassroomPostDTO classroomPostDTO){
        this.id = UUID.randomUUID();
        this.description = classroomPostDTO.description();
        this.professorId = classroomPostDTO.professorId();
        this.name = classroomPostDTO.name();
        this.studentsIds = new ArrayList<>();
    }

    public void addStudent(UUID studentId){
        studentsIds.add(studentId);
    }

    public void removeStudent(UUID studentId){
        studentsIds.remove(studentId);
    }

    public boolean containsStudent(UUID studentId){
        return studentsIds.contains(studentId);
    }
}
