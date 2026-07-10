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
    ResponseEntity<ErrorResponseDto> handlerGenericException(Exception e){

        var responseDto = new ErrorResponseDto(
          "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ErrorResponseDto> handlerEntityNotFoundException(EntityNotFoundException e){
        var responseDto = new ErrorResponseDto(
                "Not found",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    ResponseEntity<ErrorResponseDto> handlerBadRequest(EntityNotFoundException e){

        var responseDto = new ErrorResponseDto(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
}
