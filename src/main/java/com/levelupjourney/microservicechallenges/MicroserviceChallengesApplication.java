package com.levelupjourney.microservicechallenges;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MicroserviceChallengesApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MicroserviceChallengesApplication.class, args);
        String port = context.getEnvironment().getProperty("server.port", "8080");
        System.out.println("SWAGGER: http://localhost:" + port + "/swagger-ui/index.html");
    }

}
