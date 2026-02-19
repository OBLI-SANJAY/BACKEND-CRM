package com.clientconnect.leadservice.controller;
import com.clientconnect.leadservice.dto.AssignRequest;
import com.clientconnect.leadservice.dto.StageUpdateRequest;


import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.clientconnect.leadservice.model.Lead;
import com.clientconnect.leadservice.service.LeadService;
import java.util.List;
@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping
    public Lead createLead(
            @RequestBody Lead lead,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {

        return leadService.createLead(lead, role, email);
    }

    @GetMapping
    public List<Lead> getLeads(@RequestHeader("X-User-Role") String role,
                               @RequestHeader("X-User-Email") String email) {

        return leadService.getLeadsByRole(role, email);
    }

    @GetMapping("/{id}")
    public Lead getLeadById(
            @PathVariable String id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {

        return leadService.getLeadById(id, role, email);
    }

    @PatchMapping("/{id}/assign")
    public Lead assignLead(
            @PathVariable String id,
            @RequestBody AssignRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {

        return leadService.assignLead(
                id,
                request.getAssignedTo(),
                request.getAssignedRole(),
                role,
                email
        );
    }


    @DeleteMapping("/{id}")
    public void deleteLead(
            @PathVariable String id,
            @RequestHeader("X-User-Role") String role) {

        leadService.deleteLead(id, role);
    }
    @GetMapping("/search")
    public List<Lead> searchLeads(
            @RequestParam String keyword,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {

        return leadService.searchLeads(keyword, role, email);
    }
    @PatchMapping("/{id}/stage")
    public Lead updateStage(
            @PathVariable String id,
            @RequestBody StageUpdateRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {

        return leadService.updateStage(
                id,
                request.getStage(),
                role,
                email
        );
    }

}
