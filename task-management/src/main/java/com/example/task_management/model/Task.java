package com.example.task_management.model;

import java.time.LocalDateTime;

public record Task(
       Long id,
       Long creatorId,
       Long assignedUserId,
       TaskStatus status,
       LocalDateTime createDateTime,
       LocalDateTime deadlineDate,
       Priority priority
) {}
