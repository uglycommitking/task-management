package com.example.task_management.controller;

import com.example.task_management.model.Task;
import com.example.task_management.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        List<Task> tasks = taskService.getAllTasks();
        logger.info("Tasks received. Number of tasks: {}", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        Task task = taskService.findTaskById(id);
        logger.info("Task received by id : {}", id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody Task taskToCreate
    ){
        try{
            var createdTask = taskService.createTask(taskToCreate);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(createdTask);
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable("id") Long id,
            @RequestBody Task taskToUpdate
    ){
        try{
            var updatedTask = taskService.updateTask(id, taskToUpdate);
            return ResponseEntity.ok(updatedTask);

        }
        catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(
            @PathVariable("id") Long id
    ){
        taskService.deleteTaskById(id);
        return ResponseEntity.ok().build();
    }


}
