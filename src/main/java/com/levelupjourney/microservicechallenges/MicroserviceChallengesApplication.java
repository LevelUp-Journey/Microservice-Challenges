package com.levelupjourney.microservicechallenges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MicroserviceChallengesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceChallengesApplication.class, args);
        System.out.println("SWAGGER: http://localhost:8082/swagger-ui/index.html" );
    }

}
