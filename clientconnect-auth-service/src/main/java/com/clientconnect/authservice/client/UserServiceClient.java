package com.clientconnect.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.clientconnect.authservice.dto.UserProfileRequest;

@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    @PostMapping("/users")
    void createUser(@RequestBody UserProfileRequest request);
}
