package com.example.task_management.controller;

import com.example.task_management.model.Task;
import com.example.task_management.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public List<Task> getAllTasks(){
        List<Task> tasks = taskService.getAllTasks();
        logger.info("Tasks received. Number of tasks: {}", tasks.size());
        return tasks;

    }

    @GetMapping("/tasks/{id}")
    public Task getTaskById(@PathVariable Long id){
        Task task = taskService.findTaskById(id);
        logger.info("Task received by id : {}", id);
        return task;
    }
}
