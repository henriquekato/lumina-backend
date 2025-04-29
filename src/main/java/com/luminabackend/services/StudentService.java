package com.luminabackend.services;

import com.luminabackend.exceptions.EntityNotFoundException;
import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.user.User;
import com.luminabackend.repositories.user.UserRepository;
import com.luminabackend.utils.Sublist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StudentService extends UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ClassroomService classroomService;

    @Transactional
    @Override
    public void deleteById(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Student not found");
        }

        classroomService.removeStudentFromAllClassrooms(id);

        repository.deleteById(id);
    }

    public Page<Task> getStudentDoneTasks(UUID studentId, Pageable page){
        if (!repository.existsById(studentId)) throw new EntityNotFoundException("Student not found");
        List<Task> tasks = repository.findStudentDoneTasks(studentId);
        List<Task> sublist = Sublist.getSublist(tasks, page);
        return new PageImpl<>(sublist, page, tasks.size());
    }

    public Page<Task> getStudentNotDoneTasks(UUID studentId, Pageable page){
        if (!repository.existsById(studentId)) throw new EntityNotFoundException("Student not found");
        List<Task> tasks =  repository.findStudentNotDoneTasks(studentId, LocalDateTime.now());
        List<Task> sublist = Sublist.getSublist(tasks, page);
        return new PageImpl<>(sublist, page, tasks.size());
    }
}
