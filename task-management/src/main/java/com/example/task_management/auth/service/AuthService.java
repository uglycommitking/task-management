package com.example.task_management.auth.service;

import com.example.task_management.auth.mapper.AuthMapper;
import com.example.task_management.auth.model.AuthRequest;
import com.example.task_management.auth.model.AuthResponse;
import com.example.task_management.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthMapper mapper;

    public AuthService(AuthRepository authRepository, AuthMapper mapper){
        this.authRepository = authRepository;
        this.mapper = mapper;
    }

    public AuthResponse createUser(AuthRequest request){
        if(authRepository.existsByUsername(request.username())){
           throw new IllegalArgumentException("Имя пользователя " + request.username() + " занято");
        }
        var authEntity = mapper.toEntity(request);
        var response = mapper.toDomain(authRepository.save(authEntity));
        return response;
    }




}
