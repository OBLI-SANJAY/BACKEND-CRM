package com.clientconnect.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public User createUser(@RequestBody CreateUserRequest request){

        User user = new User();
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setActive(request.isActive());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setProfileCompleted(true);

        return userService.createUser(user);
    }



    @GetMapping
    public List<User> getAllUsers(@RequestParam(required = false) String role){
        if (role != null && !role.isEmpty()) {
            return userService.getUsersByRole(role);
        }
        return userService.getAllUsers();
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

  
}
