package com.example.task_management.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ сервера")
public record AuthResponse(
        @Schema(description = "id пользователя",example = "1")
        Long id,

        @Schema(description = "никнейм пользователя",example = "Andrew")
        String username
) {
}
