package com.clientconnect.authservice.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clientconnect.authservice.client.UserServiceClient;
import com.clientconnect.authservice.dto.UserProfileRequest;
import com.clientconnect.authservice.entity.User;
import com.clientconnect.authservice.repository.UserRepository;
import com.clientconnect.authservice.security.JwtUtil;
import java.util.List;
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final UserServiceClient userServiceClient;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            UserServiceClient userServiceClient) {
this.userRepository = userRepository;
this.passwordEncoder = passwordEncoder;
this.jwtUtil = jwtUtil;
this.userServiceClient = userServiceClient;
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
    public ResponseEntity<?> register(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-User-Role", required = false) String creatorRole,
            @RequestHeader(value = "X-User-Email", required = false) String creatorEmail) {

        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String role = (String) request.get("role");
        String fullName = (String) request.get("fullName");
        String phone = (String) request.get("phone");
        String address = (String) request.get("address");

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "User already exists"));
        }

        // Save auth credentials
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        User savedUser = userRepository.save(user);

        try {
            UserProfileRequest profileRequest = new UserProfileRequest();
            profileRequest.setEmail(email);
            profileRequest.setRole(role);
            profileRequest.setActive(true);
            profileRequest.setFullName(fullName);
            profileRequest.setPhone(phone);
            profileRequest.setAddress(address);
            if ("MANAGER".equals(creatorRole) && "EMPLOYEE".equals(role)) {
                profileRequest.setCreatedBy(creatorEmail);
            } else {
                profileRequest.setCreatedBy(null);
            }

            userServiceClient.createUser(profileRequest);

        } catch (Exception e) {
            e.printStackTrace();
            userRepository.delete(savedUser);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Registration failed: user profile could not be created."));
        }

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
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
