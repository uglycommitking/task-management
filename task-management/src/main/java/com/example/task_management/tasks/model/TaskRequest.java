package com.example.task_management.tasks.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskRequest(

        @NotNull
        Long creatorId,

        @NotNull
        Long assignedUserId,

        @Future
        @NotNull
        LocalDateTime deadlineDate,

        @NotNull
        Priority priority
)
{ }
