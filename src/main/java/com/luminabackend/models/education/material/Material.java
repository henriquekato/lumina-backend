package com.luminabackend.models.education.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "materials")
@Getter @Setter @NoArgsConstructor
public class Material {
    @Id
    private UUID id;
    private UUID classroomId;
    private UUID professorId;
    private String title;
    private String description;
    private String fileId;
    private LocalDateTime publicationDate;

    public Material(UUID classroomId, UUID professorId, String title, String description, String fileId) {
        this.id = UUID.randomUUID();
        this.classroomId = classroomId;
        this.professorId = professorId;
        this.title = title;
        this.description = description;
        this.fileId = fileId;
        this.publicationDate = LocalDateTime.now();
    }
}
