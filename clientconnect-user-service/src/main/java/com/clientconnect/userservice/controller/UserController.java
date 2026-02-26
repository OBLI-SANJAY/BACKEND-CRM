package com.clientconnect.userservice.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import com.clientconnect.userservice.dto.CreateUserRequest;
import com.clientconnect.userservice.entity.User;
import com.clientconnect.userservice.service.UserService;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {

        User user = new User();

        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setActive(request.isActive());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setProfileCompleted(true);
        user.setCreatedBy(request.getCreatedBy());

        return userService.createUser(user);
    }

    @GetMapping
    public List<User> getAllUsers(
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-User-Email") String email) {

        if ("ADMIN".equals(userRole)) {
            return userService.getUsersByRole("MANAGER");
        }

        if ("MANAGER".equals(userRole)) {
            return userService.getEmployeesCreatedBy(email);
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Not allowed"
        );
    }
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
    @PutMapping("/profile")
    public User completeProfile(
            @RequestBody User updatedUser,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        if (!role.equals("MANAGER") && !role.equals("EMPLOYEE")) {
            throw new RuntimeException("Only manager and employee can update profile");
        }

        return userService.completeProfile(email, updatedUser);
    }
    @GetMapping("/me")
    public User getCurrentUser(
            @RequestHeader("X-User-Email") String email) {

        return userService.getUserByEmail(email);
    }
    @GetMapping("/{id}")
    public User getUserById(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {

        User targetUser = userService.getUserById(id);

        if ("ADMIN".equals(role)) {
            return targetUser;
        }

        if ("MANAGER".equals(role)
                && "EMPLOYEE".equals(targetUser.getRole())
                && email.equals(targetUser.getCreatedBy())) {

            return targetUser;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Not authorized to view this profile"
        );
    }

  
}
