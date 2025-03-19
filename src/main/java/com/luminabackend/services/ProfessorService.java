package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteActiveProfessorException;
import com.luminabackend.exceptions.EmailAlreadyInUseException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.user.Professor;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.User;
import com.luminabackend.models.user.dto.user.UserAccessDTO;
import com.luminabackend.models.user.dto.user.UserPutDTO;
import com.luminabackend.models.user.dto.user.UserSignupDTO;
import com.luminabackend.repositories.professor.ProfessorRepository;
import com.luminabackend.utils.security.PayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository repository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public Optional<Professor> getProfessorByEmail(String email){
        return repository.findByEmail(email);
    }

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public Professor save(UserSignupDTO professorPostDTO){
        Optional<User> userByEmail = accountService.getUserByEmail(professorPostDTO.email());

        if (userByEmail.isPresent()) throw new EmailAlreadyInUseException();

        String email = professorPostDTO.email().trim();
        String password = professorPostDTO.password().trim();
        String encodedPassword = passwordEncoder.encode(password);
        String firstName = professorPostDTO.firstName().trim();
        String lastName = professorPostDTO.lastName().trim();

        Professor professor = new Professor(email, encodedPassword, firstName, lastName);
        return repository.save(professor);
    }

    public Professor edit(UUID id, UserPutDTO userPutDTO){
        Optional<Professor> professorById = getProfessorById(id);
        if(professorById.isEmpty()) throw new EntityNotFoundException("Professor not found");

        Professor professor = professorById.get();
        professor = (Professor) accountService.editUserData(professor, userPutDTO);
        return repository.save(professor);
    }

    public void deleteById(UUID id) {
        if (!existsById(id)) throw new EntityNotFoundException("Professor not found");

        if (!classroomService.getClassroomsBasedOnUserPermission(new UserAccessDTO(id, Role.PROFESSOR)).isEmpty())
            throw new CannotDeleteActiveProfessorException("Cannot delete professor because they are currently assigned to one or more active classrooms");

        repository.deleteById(id);
    }
}
