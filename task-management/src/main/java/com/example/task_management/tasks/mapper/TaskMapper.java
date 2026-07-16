package com.example.task_management.tasks.mapper;

import com.example.task_management.tasks.model.TaskResponse;
import com.example.task_management.tasks.model.TaskStatus;
import com.example.task_management.tasks.model.TaskRequest;
import com.example.task_management.tasks.repository.TaskEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskMapper {
    public TaskResponse toDomain(TaskEntity entity){
        return new TaskResponse(
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

    public TaskEntity toEntity(TaskRequest task){
        return new TaskEntity(
                null,
                task.creatorId(),
                task.assignedUserId(),
                TaskStatus.CREATED,
                LocalDateTime.now(),
                task.deadlineDate(),
                task.priority(),
                null
        );
    }
}
