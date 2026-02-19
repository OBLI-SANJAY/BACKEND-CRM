package com.clientconnect.taskservice.repository;

import com.clientconnect.taskservice.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByAssignedTo(String email);
}
