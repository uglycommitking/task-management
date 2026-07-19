package com.example.task_management.tasks.model;


import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Ответ сервера")
public record TaskResponse(

        @Schema(description = "id задачи", example = "1")
        Long id,

        @Schema(description = "id создателя", example = "1")
        Long creatorId,

        @Schema(description = "назначенный id пользователя", example = "2")
        Long assignedUserId,

        @Schema(description = "статус задачи", example = "IN_PROGRESS")
        TaskStatus status,

        @Schema(description = "время создания задачи")
        LocalDateTime createDateTime,

        @Schema(description = "дедлайн задачи")
        LocalDateTime deadlineDate,

        @Schema(description = "приоритет задачи", example = "LOW")
        Priority priority,

        @Schema(description = "время завершения задачи")
        LocalDateTime doneDateTime
)
{ }
