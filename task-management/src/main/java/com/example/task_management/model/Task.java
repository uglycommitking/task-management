package com.example.task_management.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.LocalDateTime;

public record Task(
        @Null
        Long id,

        @NotNull
        Long creatorId,

        @NotNull
        Long assignedUserId,

        TaskStatus status,

        @Null
        LocalDateTime createDateTime,

        @FutureOrPresent
        @NotNull
        LocalDateTime deadlineDate,

        @NotNull
        Priority priority,

        @Null
        LocalDateTime doneDateTime
) {}
