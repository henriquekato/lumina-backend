package com.luminabackend.controllers.admin;

import com.luminabackend.models.education.task.Task;
import com.luminabackend.models.education.task.TaskFullGetDTO;
import com.luminabackend.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController implements AdminControllerDocumentation {
    @Autowired
    private AdminService service;

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskFullGetDTO>> getAllTasks(Pageable page) {
        Page<Task> tasks = service.getAllTasks(page);
        return ResponseEntity.ok(tasks.map(TaskFullGetDTO::new));
    }
}
