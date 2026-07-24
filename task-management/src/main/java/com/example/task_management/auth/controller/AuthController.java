package com.example.task_management.auth.controller;

import com.example.task_management.auth.model.Registration;
import com.example.task_management.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid Registration request){
        authService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
