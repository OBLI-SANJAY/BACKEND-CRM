package com.clientconnect.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;

import com.clientconnect.userservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByCreatedBy(String createdBy);
}

