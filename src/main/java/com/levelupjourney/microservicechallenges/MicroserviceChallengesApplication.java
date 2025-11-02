package com.levelupjourney.microservicechallenges;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableDiscoveryClient
public class MicroserviceChallengesApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure().load();
        dotenv
            .entries()
            .forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
            );

        ConfigurableApplicationContext context = SpringApplication.run(
            MicroserviceChallengesApplication.class,
            args
        );
        String port = context
            .getEnvironment()
            .getProperty("server.port", "8080");
        System.out.println(
            "SWAGGER: http://localhost:" + port + "/swagger-ui/index.html"
        );
    }
}
