package com.example.task_management.auth.service;

import com.example.task_management.auth.mapper.AuthMapper;
import com.example.task_management.auth.model.AuthRequest;
import com.example.task_management.auth.model.AuthResponse;
import com.example.task_management.auth.repository.AuthRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final AuthMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthRepository authRepository, AuthMapper mapper, PasswordEncoder passwordEncoder){
        this.authRepository = authRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse createUser(AuthRequest request){
        if(authRepository.existsByUsername(request.username())){
           throw new IllegalArgumentException("Имя пользователя " + request.username() + " занято");
        }
        String hashed = passwordEncoder.encode(request.password());
        var authEntity = mapper.toEntity(request);
        authEntity.setPassword(hashed);
        return mapper.toDomain(authRepository.save(authEntity));
    }


}
