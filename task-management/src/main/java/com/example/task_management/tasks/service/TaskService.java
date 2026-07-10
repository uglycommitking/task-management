package com.example.task_management.tasks.service;

import com.example.task_management.tasks.repository.TaskEntity;
import com.example.task_management.tasks.model.Task;
import com.example.task_management.tasks.model.TaskStatus;
import com.example.task_management.tasks.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public Task reopenTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if(taskEntity.getStatus() != TaskStatus.DONE){
            throw new IllegalArgumentException("Task with id = " + id + " must be DONE to reopen");
        }
        var updatedTask = new TaskEntity(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                TaskStatus.IN_PROGRESS,
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority(),
                null
        );
        return entityToDomain(taskRepository.save(updatedTask));
    }

    public Task createTask(Task taskToCreate) {
        if(taskToCreate.status() != null){
            throw new IllegalArgumentException("Status must be empty");
        }

        var nowTime = LocalDateTime.now();
        if(!taskToCreate.deadlineDate().isAfter(nowTime)){
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }

        var taskEntity = new TaskEntity(
                null,
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                TaskStatus.IN_PROGRESS,
                nowTime,
                taskToCreate.deadlineDate(),
                taskToCreate.priority(),
                taskToCreate.doneDateTime()
        );
        return entityToDomain(taskRepository.save(taskEntity));
    }

    public Task findTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        return entityToDomain(taskEntity);
    }

    public List<Task> getAllTasks() {
        List<TaskEntity> allTasks = taskRepository.findAll();
        return allTasks.stream().map(task -> entityToDomain(task)).toList();
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if(taskEntity.getStatus() == TaskStatus.DONE){
            throw new IllegalStateException("Task with id = " + id + " is DONE and cannot be modified");
        }

        if(!taskToUpdate.deadlineDate().isAfter(taskEntity.getCreateDateTime())){
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }

        TaskEntity taskToSave = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                taskToUpdate.status(),
                taskEntity.getCreateDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority(),
                taskToUpdate.doneDateTime()
        );
        var task = taskRepository.save(taskToSave);
        return entityToDomain(task);
    }

    public void deleteTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));
        taskRepository.delete(taskEntity);
    }

    public Task startTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        var count = taskRepository.countByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS);

        if(count >= 5){
            throw new IllegalArgumentException("you have a lot of tasks with IN_PROGRESS status");
        }

        var taskAfterStart = new TaskEntity(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                TaskStatus.IN_PROGRESS,
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority(),
                taskEntity.getDoneDateTime()
        );
        return entityToDomain(taskRepository.save(taskAfterStart));
    }

    public Task completeTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation by id = " + id));

        if (taskEntity.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task with id = " + id + " must be IN_PROGRESS to complete");
        }

        TaskEntity taskToSave = new TaskEntity(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                TaskStatus.DONE,
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority(),
                LocalDateTime.now()
        );
        var task = taskRepository.save(taskToSave);
        return entityToDomain(task);
    }

    private Task entityToDomain(TaskEntity entity){
        return new Task(
                entity.getId(),
                entity.getCreatorId(),
                entity.getAssignedUserId(),
                entity.getStatus(),
                entity.getCreateDateTime(),
                entity.getDeadlineDate(),
                entity.getPriority(),
                entity.getDoneDateTime()
        );
    }
}
