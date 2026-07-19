package com.example.task_management.tasks.controller;

import com.example.task_management.tasks.model.TaskResponse;
import com.example.task_management.tasks.model.TaskRequest;
import com.example.task_management.tasks.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(){
        List<TaskResponse> tasks = taskService.getAllTasks();
        logger.info("Tasks received. Number of tasks: {}", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id){
        TaskResponse task = taskService.findTaskById(id);
        logger.info("Task received by id : {}", id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid TaskRequest taskToCreate
    ){
        TaskResponse createdTask = taskService.createTask(taskToCreate);
        logger.info("Task created by id = {}", createdTask.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable("id") Long id,
            @RequestBody @Valid TaskRequest taskToUpdate
    ){
        TaskResponse updatedTask = taskService.updateTask(id, taskToUpdate);
        logger.info("task updated by id = {}", updatedTask.id());
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(
            @PathVariable("id") Long id
    ){
        taskService.deleteTaskById(id);
        logger.info("Task by deleted from database");
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reopen")
    public ResponseEntity<TaskResponse> reopenTask(
            @PathVariable("id") Long id)
    {
        TaskResponse reopenedTask = taskService.reopenTask(id);
        logger.info("Task by id = {} reopend", id);
        return ResponseEntity.ok().body(reopenedTask);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<TaskResponse> startTask(
            @PathVariable("id") Long id
    ){
        TaskResponse startedTask = taskService.startTask(id);
        logger.info("Task with id = {} has started", id);
        return ResponseEntity.status(HttpStatus.OK).body(startedTask);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable("id") Long id
    ){
        TaskResponse completedTask = taskService.completeTask(id);
        logger.info("Task by id = {} completed",id);
        return ResponseEntity.status(HttpStatus.OK).body(completedTask);
    }
}
