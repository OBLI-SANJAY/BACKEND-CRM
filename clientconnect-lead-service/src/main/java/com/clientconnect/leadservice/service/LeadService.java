package com.clientconnect.leadservice.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import com.clientconnect.leadservice.model.Lead;
import com.clientconnect.leadservice.repository.LeadRepository;

@Service
public class LeadService {

    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

 
    public Lead createLead(Lead lead, String role, String email) {
    	

        if (role.equalsIgnoreCase("EMPLOYEE")) {
            throw new RuntimeException("Employees cannot create leads");
        }

      
        if (role.equalsIgnoreCase("ADMIN")) {

            if (lead.getAssignedTo() == null || lead.getAssignedTo().isEmpty()) {
                throw new RuntimeException("Admin must assign lead to a Manager");
            }

            lead.setAssignedBy(email);
            lead.setAssignedRole("MANAGER");
        }

   
        if (role.equalsIgnoreCase("MANAGER")) {

            lead.setAssignedTo(email);
            lead.setAssignedBy(email);
            lead.setAssignedRole("MANAGER");
        }

        lead.setStage("NEW");
        lead.setCreatedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());
        lead.setCreatedBy(email);
    	lead.setCreatedRole(role);


        return leadRepository.save(lead);
    }


    public List<Lead> getLeadsByRole(String role, String email) {

        if (role.equalsIgnoreCase("ADMIN")) {
            return leadRepository.findAll();
        }

        if (role.equalsIgnoreCase("MANAGER")) {
            return leadRepository.findByAssignedToOrCreatedBy(email, email);
        }

        if (role.equalsIgnoreCase("EMPLOYEE")) {
            return leadRepository.findByAssignedTo(email);
        }

        throw new RuntimeException("Unauthorized role");
    }

    public Lead getLeadById(String id, String role, String email) {

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (role.equalsIgnoreCase("ADMIN")) {
            return lead;
        }

        if (!lead.getAssignedTo().equals(email)) {
            throw new RuntimeException("Access denied");
        }

        return lead;
    }

    public void deleteLead(String id, String role) {

        if (!role.equalsIgnoreCase("ADMIN")) {
            throw new RuntimeException("Only Admin can delete leads");
        }

        leadRepository.deleteById(id);
    }

    public Lead assignLead(String id,
                           String assignedTo,
                           String assignedRole,
                           String role,
                           String email) {

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (role.equalsIgnoreCase("ADMIN")) {

            if (!assignedRole.equalsIgnoreCase("MANAGER")) {
                throw new RuntimeException("Admin can assign only to MANAGER");
            }

        } else if (role.equalsIgnoreCase("MANAGER")) {

            if (!lead.getAssignedTo().equals(email)) {
                throw new RuntimeException("Manager can assign only their leads");
            }

            if (!assignedRole.equalsIgnoreCase("EMPLOYEE")) {
                throw new RuntimeException("Manager can assign only to EMPLOYEE");
            }

        } else {
            throw new RuntimeException("Employee cannot assign leads");
        }

        lead.setAssignedTo(assignedTo);
        lead.setAssignedBy(email);
        lead.setAssignedRole(assignedRole);
        lead.setUpdatedAt(LocalDateTime.now());

        return leadRepository.save(lead);
    }
    public List<Lead> searchLeads(String keyword, String role, String email) {

        if (role.equalsIgnoreCase("ADMIN")) {
            return leadRepository.findByNameContainingIgnoreCase(keyword);
        }

        if (role.equalsIgnoreCase("MANAGER") ||
            role.equalsIgnoreCase("EMPLOYEE")) {

            return leadRepository
                    .findByAssignedToAndNameContainingIgnoreCase(email, keyword);
        }

        throw new RuntimeException("Unauthorized");
    }

    private final List<String> allowedStages = List.of(
            "NEW",
            "CONTACTED",
            "FOLLOW_UP",
            "CONVERTED",
            "LOST"
    );

    public Lead updateStage(String id, String newStage,
                            String role, String email) {

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (!allowedStages.contains(newStage)) {
            throw new RuntimeException("Invalid stage");
        }

        if (role.equalsIgnoreCase("EMPLOYEE") &&
            !lead.getAssignedTo().equals(email)) {
            throw new RuntimeException("Unauthorized");
        }

        lead.setStage(newStage);
        lead.setUpdatedAt(LocalDateTime.now());

        return leadRepository.save(lead);
    }
}
