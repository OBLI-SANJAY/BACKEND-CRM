package com.clientconnect.leadservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.clientconnect.leadservice.model.Lead;

import java.util.Optional;
import java.util.List;

public interface LeadRepository extends MongoRepository<Lead, String> {

    Optional<Lead> findByEmail(String email);
    List<Lead> findByAssignedTo(String assignedTo);
    Optional<Lead> findByPhone(String phone);
    List<Lead> findByPhoneContaining(String phone);
    List<Lead> findByEmailContaining(String email);
    List<Lead> findByNameContainingIgnoreCase(String name);
    List<Lead> findByAssignedToAndPhoneContaining(String assignedTo, String phone);
    List<Lead> findByAssignedToAndEmailContaining(String assignedTo, String email);
    List<Lead> findByAssignedToAndNameContainingIgnoreCase(String assignedTo, String name);
    
}
