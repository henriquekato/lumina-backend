package com.luminabackend.models.education.material;

import com.luminabackend.models.education.file.FileInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private List<FileInfo> files;
    private LocalDateTime publicationDate;

    public Material(UUID classroomId, UUID professorId, String title, String description) {
        this.id = UUID.randomUUID();
        this.classroomId = classroomId;
        this.professorId = professorId;
        this.title = title;
        this.description = description;
        this.files = new ArrayList<>();
        this.publicationDate = LocalDateTime.now();
    }

    public void addFile(FileInfo file){
        this.files.add(file);
    }

    public void removeFile(FileInfo file){
        this.files.remove(file);
    }
}
