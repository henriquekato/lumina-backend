package com.luminabackend.services;

import com.luminabackend.exceptions.CannotDeleteActiveProfessorException;
import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.Role;
import com.luminabackend.models.user.dto.UserAccessDTO;
import com.luminabackend.repositories.user.UserRepository;
import com.luminabackend.utils.Sublist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProfessorService extends UserService {
    @Autowired
    private UserRepository repository;

    public Page<Task> getProfessorTasks(UUID professorId, Pageable page){
        if (!repository.existsById(professorId)) throw new EntityNotFoundException("Professor not found");
        List<Task> tasks = repository.findProfessorTasks(professorId, LocalDateTime.now());
        List<Task> sublist = Sublist.getSublist(tasks, page);
        return new PageImpl<>(sublist, page, tasks.size());
    }
}
