package com.luminabackend.middlewares;

import com.luminabackend.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("taskExistance")
public class TaskExistenceMiddleware {
    @Autowired
    private TaskService taskService;

    public boolean verify(UUID taskId){
        taskService.checkTaskExistanceById(taskId);
        return true;
    }
}
