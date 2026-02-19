package com.clientconnect.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public User createUser(@RequestBody User user){
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
  
}
