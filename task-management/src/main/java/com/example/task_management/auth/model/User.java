package com.example.task_management.auth.model;

import jakarta.validation.constraints.NotBlank;

public record User(
        @NotBlank
        String username,
        @NotBlank
        String password
){}