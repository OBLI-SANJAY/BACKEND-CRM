package com.clientconnect.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ClientconnectEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientconnectEurekaServerApplication.class, args);
    }
}
