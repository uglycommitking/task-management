package com.example.task_management.tasks.repository;


import com.example.task_management.tasks.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    int countByAssignedUserIdAndStatus(Long assignedUserId, TaskStatus status);
}
