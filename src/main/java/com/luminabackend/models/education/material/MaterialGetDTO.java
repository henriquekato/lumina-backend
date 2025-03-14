package com.luminabackend.models.education.material;

import java.time.LocalDateTime;
import java.util.UUID;

public record MaterialGetDTO (
        UUID id,
        UUID classroomId,
        String title,
        String description,
        String fileId,
        LocalDateTime publicationDate
){
    public MaterialGetDTO(Material material){
        this(
                material.getId(),
                material.getClassroomId(),
                material.getTitle(),
                material.getDescription(),
                material.getFileId(),
                material.getPublicationDate()
        );
    }
}
