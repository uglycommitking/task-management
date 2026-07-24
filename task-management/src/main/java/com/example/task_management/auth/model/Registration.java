package com.example.task_management.auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Регистрация пользователя")
public record Registration(
        @Schema(description = "Логин",example = "Andrew")
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z]*$", message = "Допустимы только английские буквы")
        @Length(min = 3,max = 20)

        String username,

        @Schema(description = "Пароль", example = "Lid123")
        @NotBlank
        @Length(min = 4, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Пароль должен состоять только из английских букв или цифр")

        String password
){}