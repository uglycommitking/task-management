package com.example.task_management.tasks.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


@Schema(description = "Задача")
public record TaskRequest(

        @Schema(description = "id создателя", example = "1")
        @NotNull
        Long creatorId,

        @Schema(description = "назначенный id пользователя", example = "2")
        @NotNull
        Long assignedUserId,

        @Schema(description = "дедлайн задачи")
        @Future
        @NotNull
        LocalDateTime deadlineDate,

        @Schema(description = "приоритет задачи", example = "LOW")
        @NotNull
        Priority priority
)
{ }
