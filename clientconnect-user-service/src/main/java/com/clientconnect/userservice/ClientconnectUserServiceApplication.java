package com.clientconnect.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClientconnectUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientconnectUserServiceApplication.class, args);
    }
}
