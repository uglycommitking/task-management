package com.example.task_management.auth.service;

import com.example.task_management.auth.model.Registration;
import com.example.task_management.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public Registration createUser(Registration request){

    }




}
