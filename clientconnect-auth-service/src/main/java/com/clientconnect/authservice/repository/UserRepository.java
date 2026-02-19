package com.clientconnect.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.clientconnect.authservice.entity.User;

import java.util.Optional;
import java.util.List;
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    List<User> findByRole(String role);
    boolean existsByEmail(String email);

}
