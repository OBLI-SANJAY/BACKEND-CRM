package com.clientconnect.customerservice.service;

import com.clientconnect.customerservice.dto.AssignRequest;
import com.clientconnect.customerservice.dto.CustomerRequest;
import com.clientconnect.customerservice.exception.ResourceNotFoundException;
import com.clientconnect.customerservice.exception.UnauthorizedException;
import com.clientconnect.customerservice.model.Customer;
import com.clientconnect.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer createCustomer(CustomerRequest request,
                                   String email,
                                   String role) {

        if (role.equals("EMPLOYEE")) {
            throw new UnauthorizedException("Employees cannot create customers");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setCompany(request.getCompany());

        customer.setCreatedBy(email);
        customer.setCreatedRole(role);
        customer.setCreatedAt(LocalDateTime.now());

        if (role.equals("ADMIN")) {
            customer.setAssignedTo(request.getAssignedTo());
            customer.setAssignedRole("MANAGER");
        } else if (role.equals("MANAGER")) {
            customer.setAssignedTo(request.getAssignedTo());
            customer.setAssignedRole("EMPLOYEE");
        }

        customer.setAssignedBy(email);

        return repository.save(customer);
    }

    public List<Customer> getCustomers(String email, String role) {

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


    public Customer getCustomerById(String id,
                                    String email,
                                    String role) {

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!role.equals("ADMIN") &&
                !customer.getAssignedTo().equals(email)) {
            throw new UnauthorizedException("Access denied");
        }

        return customer;
    }

    public Customer assignCustomer(String id,
                                   AssignRequest request,
                                   String email,
                                   String role) {

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (role.equals("ADMIN")) {
            customer.setAssignedTo(request.getAssignedTo());
            customer.setAssignedRole("MANAGER");
        }
        else if (role.equals("MANAGER")) {

            if (!customer.getAssignedTo().equals(email)) {
                throw new UnauthorizedException("Not your customer");
            }

            customer.setAssignedTo(request.getAssignedTo());
            customer.setAssignedRole("EMPLOYEE");
        }
        else {
            throw new UnauthorizedException("Employees cannot assign");
        }

        customer.setAssignedBy(email);
        customer.setUpdatedAt(LocalDateTime.now());

        return repository.save(customer);
    }

    public void deleteCustomer(String id, String role) {

        if (!role.equals("ADMIN")) {
            throw new UnauthorizedException("Only admin can delete");
        }

        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        repository.delete(customer);
    }
}
