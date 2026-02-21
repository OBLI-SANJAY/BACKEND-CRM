package com.clientconnect.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClientconnectAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientconnectAuthServiceApplication.class, args);
    }
}
