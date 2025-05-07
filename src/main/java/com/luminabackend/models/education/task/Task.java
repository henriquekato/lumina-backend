package com.luminabackend.models.education.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private UUID classroomId;
    private String classroomName;

    public Task(TaskCreateDTO taskCreateDTO) {
        this.id = UUID.randomUUID();
        this.title = taskCreateDTO.title();
        this.description = taskCreateDTO.description();
        this.dueDate = taskCreateDTO.dueDate();
        this.classroomId = taskCreateDTO.classroomId();
    }

    public boolean isDueDateExpired(){
        return dueDate.isBefore(LocalDateTime.now());
    }
}
