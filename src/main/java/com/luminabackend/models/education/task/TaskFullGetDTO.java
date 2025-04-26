package com.luminabackend.models.education.task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskFullGetDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime dueDate,
        String classroomName
){
    public TaskFullGetDTO(Task task){
        this(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getClassroomName());
    }
}
