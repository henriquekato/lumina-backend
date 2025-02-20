package com.luminabackend.models.education.task;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "tasks")
@Getter @Setter
public class Task {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private UUID classroomId;

    public Task(){
        this.id = UUID.randomUUID();
    }

    public Task(TaskPostDTO taskPostDTO) {
        this.id = UUID.randomUUID();
        this.title = taskPostDTO.title();
        this.description = taskPostDTO.description();
        this.dueDate = taskPostDTO.dueDate();
        this.classroomId = taskPostDTO.classroomId();
    }
}
