package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.exceptions.StudentAlreadyInClassroomException;
import com.luminabackend.models.education.classroom.Classroom;
import com.luminabackend.models.education.classroom.ClassroomPostDTO;
import com.luminabackend.models.education.classroom.ClassroomPutDTO;
import com.luminabackend.models.education.classroom.ClassroomWithRelationsDTO;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.professor.ProfessorGetDTO;
import com.luminabackend.models.user.dto.student.StudentGetDTO;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.repositories.classroom.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClassroomService {
    @Autowired
    private ClassroomRepository repository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private MaterialService materialService;

    public List<Classroom> getClassroomsBasedOnUserAccess(UserAccessDTO userAccessDTO) {
        if (userAccessDTO.role().equals(Role.ADMIN))
            return repository.findAll();

        if (userAccessDTO.role().equals(Role.PROFESSOR))
            return repository.findAllByProfessorId(userAccessDTO.id());

        return repository.findAllByStudentsIdsContains(userAccessDTO.id());
    }

    public Page<Classroom> getPaginatedClassroomsBasedOnUserAccess(UserAccessDTO userAccessDTO, Pageable page) {
        if (userAccessDTO.role().equals(Role.ADMIN))
            return repository.findAll(page);

        if (userAccessDTO.role().equals(Role.PROFESSOR))
            return repository.findAllByProfessorId(userAccessDTO.id(), page);

        return repository.findAllByStudentsIdsContains(userAccessDTO.id(), page);
    }

    public Classroom getClassroomById(UUID classroomId) {
        Optional<Classroom> classroomById = repository.findById(classroomId);
        if (classroomById.isEmpty()) throw new EntityNotFoundException("Classroom not found");
        return classroomById.get();
    }

    public Classroom getClassroomBasedOnUserPermission(UUID classroomId, UserAccessDTO userAccessDTO) {
        Classroom classroom = getClassroomById(classroomId);
        permissionService.checkAccessToClassroom(classroom, userAccessDTO);
        return classroom;
    }

    public Optional<Classroom> getClassroomByName(String name) {
        return repository.findByName(name);
    }

    public ClassroomWithRelationsDTO getClassroomWithRelations(Classroom classroom){
        Optional<Professor> professorById = professorService.getProfessorById(classroom.getProfessorId());
        ProfessorGetDTO professor = professorById
                .map(ProfessorGetDTO::new)
                .orElseThrow(IllegalStateException::new);

        List<StudentGetDTO> students = studentService
                .getAllStudentsById(classroom.getStudentsIds())
                .stream()
                .map(StudentGetDTO::new)
                .toList();

        return new ClassroomWithRelationsDTO(classroom.getId(), professor, students);
    }

    public boolean existsById(UUID classroomId) {
        return repository.existsById(classroomId);
    }

    public Classroom save(ClassroomPostDTO classroomPostDTO) {
        Classroom classroom = new Classroom(classroomPostDTO);

        classroom.setName(classroom.getName().trim());
        if (classroomPostDTO.description() != null) classroom.setDescription(classroomPostDTO.description().trim());

        return repository.save(classroom);
    }

    public Classroom edit(UUID classroomId, ClassroomPutDTO classroomPutDTO) {
        Classroom classroom = getClassroomById(classroomId);

        if (classroomPutDTO.name() != null)
            classroom.setName(classroomPutDTO.name().trim());

        if (classroomPutDTO.description() != null)
            classroom.setDescription(classroomPutDTO.description().trim());

        UUID professorId = classroomPutDTO.professorId();
        if (professorId != null) {
            if (!professorService.existsById(professorId))
                throw new EntityNotFoundException("Professor not found");
            classroom.setProfessorId(professorId);
        }

        return repository.save(classroom);
    }

    public void deleteById(UUID classroomId) {
        if (!existsById(classroomId))
            throw new EntityNotFoundException("Classroom not found");

        taskService.deleteAllByClassroomId(classroomId);

        materialService.deleteAllByClassroomId(classroomId);

        repository.deleteById(classroomId);
    }

    public void addStudentToClassroom(UUID studentId, Classroom classroom) {
        if (classroom.containsStudent(studentId))
            throw new StudentAlreadyInClassroomException("The student you are trying to add is already in this class");

        classroom.addStudent(studentId);
        repository.save(classroom);
    }

    public void removeStudentFromClassroom(UUID studentId, Classroom classroom) {
        if (!classroom.containsStudent(studentId))
            throw new EntityNotFoundException("The student you are trying to remove is not in this class");

        classroom.removeStudent(studentId);

        repository.save(classroom);
    }

    public void removeStudentFromAllClassrooms(UUID studentId){
        repository.pullStudentFromAllClassrooms(studentId);
    }
}
