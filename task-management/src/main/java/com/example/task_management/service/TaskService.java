package com.example.task_management.service;

import com.example.task_management.model.Priority;
import com.example.task_management.model.Task;
import com.example.task_management.model.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class TaskService {

    private final HashMap<Long, Task> tasksMap = new HashMap<>();

    public TaskService(){
        List.of(
                new Task(
                        1L,
                        10L,
                        10L,
                        TaskStatus.CREATED,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(5),
                        Priority.LOW
                ),
                new Task(
                        2L,
                        10L,
                        10L,
                        TaskStatus.DONE,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(5),
                        Priority.HIGH
                ),
                new Task(
                        3L,
                        10L,
                        10L,
                        TaskStatus.IN_PROGRESS,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(5),
                        Priority.MEDIUM
                )
        ).forEach(r -> tasksMap.put(r.id(), r));
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
