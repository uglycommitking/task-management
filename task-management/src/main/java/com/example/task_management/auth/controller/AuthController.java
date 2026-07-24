package com.example.task_management.auth.controller;

import com.example.task_management.auth.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

//    private final AuthService authService;
//
//    public AuthController(AuthService authService){
//        this.authService = authService;
//    }

    @GetMapping("registration")
    public ResponseEntity<Void> registration(@RequestBody User user){
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
