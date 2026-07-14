package com.example.task_management.web;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDto> handleGenericException(Exception e){

        var responseDto = new ErrorResponseDto(
          "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e){
        var responseDto = new ErrorResponseDto(
                "Not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            MethodArgumentNotValidException.class
    })
    ResponseEntity<ErrorResponseDto> handleBadRequest(Exception e){

        var responseDto = new ErrorResponseDto(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<ErrorResponseDto> handleIllegalStateException(IllegalStateException e){
        var responseDto = new ErrorResponseDto(
                "Conflict",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
    }
}
