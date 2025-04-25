package com.luminabackend.models.education.classroom;

import com.luminabackend.services.ProfessorService;

import java.util.UUID;

public record ClassroomFrontendDTO(
        UUID id,
        String name,
        String tag,
        int studentsCounter,
        String professor
) {
    public ClassroomFrontendDTO(UUID id, String name, String tag, int studentsCounter, String professor){
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.studentsCounter = studentsCounter;
        this.professor = professor;
    }

    public static String generateTag(String name){
        if (name == null || name.isEmpty()) return "";
        String[] words = name.split("\\s+");
        StringBuilder tag = new StringBuilder();

        for (String word: words){
            tag.append(word.charAt(0));
        }

        return tag.toString().toUpperCase();
    }
}
