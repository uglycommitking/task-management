package com.example.task_management.tasks.repository;

import com.example.task_management.tasks.model.Priority;
import com.example.task_management.tasks.model.TaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "tasks")
@Entity
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "assigned_user_id", nullable = false)
    private Long assignedUserId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column(name = "create_date_time", nullable = false)
    private LocalDateTime createDateTime;

    @Column(name = "deadline_date", nullable = false)
    private LocalDateTime deadlineDate;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(name = "done_date_time")
    private LocalDateTime doneDateTime;

    public TaskEntity() {
    }

    public TaskEntity(Long id, Long creatorId, Long assignedUserId, TaskStatus status, LocalDateTime createDateTime, LocalDateTime deadlineDate, Priority priority, LocalDateTime doneDateTime) {
        this.id = id;
        this.creatorId = creatorId;
        this.assignedUserId = assignedUserId;
        this.status = status;
        this.createDateTime = createDateTime;
        this.deadlineDate = deadlineDate;
        this.priority = priority;
        this.doneDateTime = doneDateTime;
    }


    public LocalDateTime getDoneDateTime() {
        return doneDateTime;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Long getId() {
        return id;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setDoneDateTime(LocalDateTime doneDateTime) {
        this.doneDateTime = doneDateTime;
    }
}
