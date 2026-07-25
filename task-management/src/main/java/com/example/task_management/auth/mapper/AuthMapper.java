package com.example.task_management.auth.mapper;

import com.example.task_management.auth.model.AuthRequest;
import com.example.task_management.auth.model.AuthResponse;
import com.example.task_management.auth.repository.AuthEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toDomain(AuthEntity entity){
        return new AuthResponse(
                entity.getId(),
                entity.getUsername()
        );
    }

    public AuthEntity toEntity(AuthRequest request){
        return new AuthEntity(
                null,
                request.username(),
                request.password()
        );
    }


}
