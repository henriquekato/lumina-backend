package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteActiveProfessorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.models.user.dto.user.UserNewDataDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.professor.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private ClassroomService classroomService;

    public List<Professor> getAllProfessors() {
        return repository.findAll();
    }

    public Page<Professor> getPaginatedProfessors(Pageable page) {
        return repository.findAll(page);
    }

    public Optional<Professor> getProfessorById(UUID id) {
        return repository.findById(id);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Professor save(UserSignupDTO professorPostDTO){
        userService.validateUserSignupData(professorPostDTO);

        Optional<User> userByEmail = userService.getUserByEmail(professorPostDTO.email());
        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        UserNewDataDTO userNewDataDTO = userService.prepareUserDataToSave(professorPostDTO);

        Professor professor = new Professor(userNewDataDTO);
        return repository.save(professor);
    }

    public Professor edit(UUID id, UserPutDTO userPutDTO){
        Optional<Professor> professorById = getProfessorById(id);
        if(professorById.isEmpty()) throw new EntityNotFoundException("Professor not found");

        Professor professor = professorById.get();
        professor = (Professor) userService.editUserData(professor, userPutDTO);
        return repository.save(professor);
    }

    public void deleteById(UUID id) {
        if (!existsById(id)) throw new EntityNotFoundException("Professor not found");

        if (!classroomService.getClassroomsBasedOnUserAccess(new UserAccessDTO(id, Role.PROFESSOR)).isEmpty())
            throw new CannotDeleteActiveProfessorException("Cannot delete professor because they are currently assigned to one or more active classrooms");

        repository.deleteById(id);
    }
}
