package com.clientconnect.customerservice.controller;

import com.clientconnect.customerservice.dto.AssignRequest;
import com.clientconnect.customerservice.dto.CustomerRequest;
import com.clientconnect.customerservice.model.Customer;
import com.clientconnect.customerservice.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public Customer createCustomer(
            @RequestBody CustomerRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        return service.createCustomer(request, email, role);
    }

    @GetMapping
    public List<Customer> getCustomers(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        return service.getCustomers(email, role);
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(
            @PathVariable String id,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        return service.getCustomerById(id, email, role);
    }

    @PatchMapping("/{id}/assign")
    public Customer assignCustomer(
            @PathVariable String id,
            @RequestBody AssignRequest request,
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        return service.assignCustomer(id, request, email, role);
    }

    @DeleteMapping("/{id}")
    public String deleteCustomer(
            @PathVariable String id,
            @RequestHeader("X-User-Role") String role) {

        service.deleteCustomer(id, role);
        return "Customer deleted successfully";
    }
}
