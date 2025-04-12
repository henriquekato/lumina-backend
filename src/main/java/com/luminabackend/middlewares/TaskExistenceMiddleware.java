package com.luminabackend.middlewares;

import com.luminabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("taskExistence")
public class TaskExistenceMiddleware {
    @Autowired
    private TaskService taskService;

    public boolean verify(UUID taskId){
        taskService.checkTaskExistenceById(taskId);
        return true;
    }
}
