package com.example.task_management.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    boolean existsByUsername(String username);
    Optional<AuthEntity> findByUsername(String username);
}
