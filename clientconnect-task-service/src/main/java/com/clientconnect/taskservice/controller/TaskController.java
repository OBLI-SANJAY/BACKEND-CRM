package com.clientconnect.taskservice.controller;

import com.clientconnect.taskservice.dto.*;
import com.clientconnect.taskservice.model.Task;
import com.clientconnect.taskservice.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public Task createTask(@RequestBody TaskRequest request,
                           @RequestHeader("X-User-Email") String email,
                           @RequestHeader("X-User-Role") String role) {
        return service.createTask(request, email, role);
    }

    @GetMapping
    public List<Task> getTasks(@RequestHeader("X-User-Email") String email,
                               @RequestHeader("X-User-Role") String role) {
        return service.getTasks(email, role);
    }

    @PatchMapping("/{id}/stage")
    public Task updateStage(@PathVariable String id,
                            @RequestBody StageUpdateRequest request,
                            @RequestHeader("X-User-Email") String email,
                            @RequestHeader("X-User-Role") String role) {
        return service.updateStage(id, request, email, role);
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable String id,
                             @RequestHeader("X-User-Role") String role) {
        service.deleteTask(id, role);
        return "Task deleted";
    }



}
