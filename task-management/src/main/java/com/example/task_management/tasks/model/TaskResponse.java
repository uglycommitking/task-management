package com.example.task_management.tasks.model;


import java.time.LocalDateTime;

public record TaskResponse(

        Long id,

        Long creatorId,

        Long assignedUserId,

        TaskStatus status,

        LocalDateTime createDateTime,

        LocalDateTime deadlineDate,

        Priority priority,

        LocalDateTime doneDateTime

)
{ }
