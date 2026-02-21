package com.clientconnect.customerservice.repository;

import com.clientconnect.customerservice.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    List<Customer> findByAssignedTo(String email);

    List<Customer> findByAssignedToAndNameContainingIgnoreCase(String email, String keyword);
    List<Customer> findByNameContainingIgnoreCase(String keyword);
    List<Customer> findByAssignedToOrCreatedBy(String assignedTo, String createdBy);

}
