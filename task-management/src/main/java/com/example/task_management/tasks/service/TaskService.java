package com.example.task_management.tasks.service;

import com.example.task_management.tasks.mapper.TaskMapper;
import com.example.task_management.tasks.model.TaskResponse;
import com.example.task_management.tasks.model.TaskRequest;
import com.example.task_management.tasks.repository.TaskEntity;
import com.example.task_management.tasks.model.TaskStatus;
import com.example.task_management.tasks.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository taskRepository, TaskMapper mapper){
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    public TaskResponse reopenTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));

        if(taskEntity.getStatus() != TaskStatus.DONE){
            throw new IllegalStateException("Task with id = " + id + " must be DONE to reopen");
        }

        taskEntity.setStatus(TaskStatus.IN_PROGRESS);

        return mapper.toDomain(taskRepository.save(taskEntity));
    }

    public TaskResponse createTask(TaskRequest taskToCreate) {

        var taskToSave = mapper.toEntity(taskToCreate);

        return mapper.toDomain(taskRepository.save(taskToSave));
    }

    public TaskResponse findTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found by id = " + id));        return mapper.toDomain(taskEntity);
    }

    public List<TaskResponse> getAllTasks() {
        List<TaskEntity> allTasks = taskRepository.findAll();
        return allTasks.stream().map(mapper::toDomain).toList();
    }

    public TaskResponse updateTask(Long id, TaskRequest taskToUpdate) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));

        if(taskEntity.getStatus() == TaskStatus.DONE){
            throw new IllegalStateException("Task with id = " + id + " is DONE and cannot be modified");
        }

        if(!taskToUpdate.deadlineDate().isAfter(taskEntity.getCreateDateTime())){
            throw new IllegalArgumentException("Start date must be 1 day earlier than end date");
        }


        taskEntity.setCreatorId(taskToUpdate.creatorId());
        taskEntity.setAssignedUserId(taskToUpdate.assignedUserId());
        taskEntity.setDeadlineDate(taskToUpdate.deadlineDate());
        taskEntity.setPriority(taskToUpdate.priority());


        var task = taskRepository.save(taskEntity);
        return mapper.toDomain(task);
    }

    public void deleteTaskById(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));
        taskRepository.delete(taskEntity);
    }

    public TaskResponse startTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));

        var count = taskRepository.countByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), TaskStatus.IN_PROGRESS);

        if(count >= 5){
            throw new IllegalStateException("you have a lot of tasks with IN_PROGRESS status");
        }

        taskEntity.setStatus(TaskStatus.IN_PROGRESS);


        return mapper.toDomain(taskRepository.save(taskEntity));
    }

    public TaskResponse completeTask(Long id) {
        TaskEntity taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));

        if (taskEntity.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Task with id = " + id + " must be IN_PROGRESS to complete");
        }

        taskEntity.setStatus(TaskStatus.DONE);
        taskEntity.setDoneDateTime(LocalDateTime.now());

        var task = taskRepository.save(taskEntity);
        return mapper.toDomain(task);
    }
}
