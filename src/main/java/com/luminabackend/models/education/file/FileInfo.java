package com.luminabackend.models.education.file;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class FileInfo {
    private String id;
    private String name;
    private LocalDateTime uploadDate;

    public FileInfo(String id, String name) {
        this.id = id;
        this.name = name;
        this.uploadDate = LocalDateTime.now();
    }
}
