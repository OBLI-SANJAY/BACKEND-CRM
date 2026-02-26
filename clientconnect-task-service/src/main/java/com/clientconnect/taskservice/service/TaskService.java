package com.clientconnect.taskservice.service;

import com.clientconnect.taskservice.dto.*;
import com.clientconnect.taskservice.exception.*;
import com.clientconnect.taskservice.model.Task;
import com.clientconnect.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task createTask(TaskRequest request,
                           String email,
                           String role) {

        if (role.equals("EMPLOYEE")) {
            throw new UnauthorizedException("Employees cannot create tasks");
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStage("TODO");

        task.setCreatedBy(email);
        task.setCreatedRole(role);
        task.setCreatedAt(LocalDateTime.now());

        if (role.equals("ADMIN")) {
            task.setAssignedTo(request.getAssignedTo());
            task.setAssignedRole("MANAGER");
        } else {
            task.setAssignedTo(request.getAssignedTo());
            task.setAssignedRole("EMPLOYEE");
        }

        task.setAssignedBy(email);

        return repository.save(task);
    }

    public List<Task> getTasks(String email, String role) {

        switch (role) {

            case "ADMIN":
                return repository.findAll();

            case "MANAGER":
                return repository.findByAssignedToOrCreatedBy(email, email);

            case "EMPLOYEE":
                return repository.findByAssignedTo(email);

            default:
                throw new UnauthorizedException("Invalid role");
        }
    }


    public Task updateStage(String id,
                            StageUpdateRequest request,
                            String email,
                            String role) {

        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!role.equals("ADMIN") &&
                !task.getAssignedTo().equals(email)) {
            throw new UnauthorizedException("Not your task");
        }

        task.setStage(request.getStage());
        task.setUpdatedAt(LocalDateTime.now());

        return repository.save(task);
    }

    public void deleteTask(String id, String role) {

    	   if (!role.equalsIgnoreCase("ADMIN") &&
    		        !role.equalsIgnoreCase("MANAGER")) {
            throw new UnauthorizedException("Only admin or manager can delete");
        }

        repository.deleteById(id);
    }
  

}
