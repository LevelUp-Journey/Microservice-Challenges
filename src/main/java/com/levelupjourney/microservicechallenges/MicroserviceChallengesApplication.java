package com.levelupjourney.microservicechallenges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroserviceChallengesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceChallengesApplication.class, args);
        System.out.println("SWAGGER: http://localhost:8080/swagger-ui/index.html" );
    }

}
