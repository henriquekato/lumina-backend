package com.luminabackend.models.education.task;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCreateDTO(
        String title,
        String description,
        LocalDateTime dueDate,
        UUID classroomId
){
    public TaskCreateDTO(TaskPostDTO taskPostDTO, UUID classroomId){
        this(taskPostDTO.title(), taskPostDTO.description(), taskPostDTO.dueDate(), classroomId);
    }
}
