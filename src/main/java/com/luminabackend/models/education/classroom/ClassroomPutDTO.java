package com.luminabackend.models.education.classroom;

import java.util.UUID;

public record ClassroomPutDTO(
        String name,
        String description,
        UUID professorId
) {
}
