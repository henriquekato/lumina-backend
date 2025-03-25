package com.luminabackend.middlewares;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.classroom.ClassroomPutDTO;
import com.luminabackend.services.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("professorExistance")
public class ProfessorExistanceMiddleware {
    @Autowired
    private ProfessorService professorService;

    public boolean verify(ClassroomPutDTO classroomPutDTO){
        if (classroomPutDTO.professorId() != null && !professorService.existsById(classroomPutDTO.professorId()))
            throw new EntityNotFoundException("Professor not found");
        return true;
    }
}
