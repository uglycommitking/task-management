package com.example.task_management.service;

import com.example.task_management.controller.TaskController;
import com.example.task_management.model.Task;
import com.example.task_management.model.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    //private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final HashMap<Long, Task> tasksMap;
    AtomicLong taskId;

    public TaskService(){
        tasksMap = new HashMap<>();
        taskId = new AtomicLong();
    }

    public Task createTask(Task taskToCreate) {
        if(taskToCreate.id() != null){
            throw new IllegalArgumentException("Id should be empty");
        }
        if(taskToCreate.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }
        var newTask = new Task(
                taskId.incrementAndGet(),
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                TaskStatus.CREATED,
                taskToCreate.createDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );
        tasksMap.put(newTask.id(), newTask);
        return newTask;
    }

    public Task findTaskById(Long id) {
        if(!tasksMap.containsKey(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Task with " + id + " not found");
        }
        return tasksMap.get(id);
    }

    public List<Task> getAllTasks() {
        return tasksMap.values().stream().toList();
    }
}
