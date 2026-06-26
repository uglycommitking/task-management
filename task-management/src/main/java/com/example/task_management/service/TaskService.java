package com.example.task_management.service;

import com.example.task_management.entity.TaskEntity;
import com.example.task_management.model.Task;
import com.example.task_management.model.TaskStatus;
import com.example.task_management.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
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
            throw new IllegalArgumentException("Id must be DONE for this method");
        }
        var updatedTask = new TaskEntity(
                taskEntity.getId(),
                taskEntity.getCreatorId(),
                taskEntity.getAssignedUserId(),
                TaskStatus.IN_PROGRESS,
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority()
        );
        return entityToDomain(taskRepository.save(updatedTask));
    }

    public Task createTask(Task taskToCreate) {
        if(taskToCreate.id() != null){
            throw new IllegalArgumentException("Id must be empty");
        }
        if(taskToCreate.status() != null){
            throw new IllegalArgumentException("Status must be empty");
        }
        var taskEntity = new TaskEntity(
                null,
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                TaskStatus.CREATED,
                taskToCreate.createDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
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
        TaskEntity taskToSave = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                taskToUpdate.status(),
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
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

        if(taskEntity.getAssignedUserId() == null){
            throw new IllegalArgumentException("assignedUserId mustn't be empty");
        }

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
                taskEntity.getPriority()
        );
        return entityToDomain(taskRepository.save(taskAfterStart));
    }

    private Task entityToDomain(TaskEntity entity){
        return new Task(
                entity.getId(),
                entity.getCreatorId(),
                entity.getAssignedUserId(),
                entity.getStatus(),
                entity.getCreateDateTime(),
                entity.getDeadlineDate(),
                entity.getPriority()
        );
    }
}
