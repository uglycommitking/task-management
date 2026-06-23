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
import java.util.NoSuchElementException;
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
            throw new NoSuchElementException("Task with id = " + id + " not found");
        }
        return tasksMap.get(id);
    }

    public List<Task> getAllTasks() {
        return tasksMap.values().stream().toList();
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        if(!tasksMap.containsKey(id)){
            throw new NoSuchElementException("Task with id = " + id + " not found");
        }
        var task = tasksMap.get(id);
        if(task.status() == TaskStatus.DONE){
            throw new IllegalStateException("Task with id = " + id + " is DONE and cannot be modified");
        }
        var updatedTask = new Task(
                task.id(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                taskToUpdate.status(),
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        tasksMap.put(task.id(), updatedTask);
        return updatedTask;
    }

    public void deleteTaskById(Long id) {
        if(!tasksMap.containsKey(id)){
            throw new NoSuchElementException("Task with id = " + id + " not found");
        }
        tasksMap.remove(id);
    }
}
