package com.example.task_management.tasks.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Priority {
    LOW, MEDIUM, HIGH;

    @JsonCreator
    public static Priority fromString(String value) {
        return Priority.valueOf(value.toUpperCase());
    }
}


