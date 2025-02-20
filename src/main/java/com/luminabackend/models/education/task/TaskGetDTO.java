package com.luminabackend.models.education.task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskGetDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime dueDate
){
    public TaskGetDTO(Task task){
        this(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate());
    }
}
