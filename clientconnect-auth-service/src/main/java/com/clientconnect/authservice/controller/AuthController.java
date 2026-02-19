package com.clientconnect.authservice.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clientconnect.authservice.entity.User;
import com.clientconnect.authservice.repository.UserRepository;
import com.clientconnect.authservice.security.JwtUtil;
import java.util.List;
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping("/managers")
    public List<User> getAllManagers(
            @RequestHeader("X-User-Role") String role) {

        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only Admin can view managers");
        }

        return userRepository.findByRole("MANAGER");
    }
    @GetMapping("/employees")
    public List<User> getAllEmployees(
            @RequestHeader("X-User-Role") String role) {

        if (!role.equalsIgnoreCase("MANAGER") &&
            !role.equalsIgnoreCase("ADMIN")) {

            throw new RuntimeException("Not authorized");
        }

        return userRepository.findByRole("EMPLOYEE");
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        // 🔥 CHECK IF USER ALREADY EXISTS
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "User already exists"));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of("message", "User registered successfully")
        );
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "invalid user please signup"));
        }

        User dbUser = optionalUser.get();

        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "invalid user please signup"));
        }

        String token = jwtUtil.generateToken(
                dbUser.getEmail(),
                dbUser.getRole()
        );

        return ResponseEntity.ok(token);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(
            @RequestHeader("X-User-Email") String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        userRepository.delete(userOptional.get());

        return ResponseEntity.ok(
                Map.of("message", "Account deleted successfully")
        );
    }

}
