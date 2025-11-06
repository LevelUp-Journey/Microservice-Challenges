package com.levelupjourney.microservicechallenges.shared.infrastructure.documentation.openapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


@Configuration
public class OpenApiConfiguration {

    String applicationName = "Microservice Challenges API";
    String applicationDescription = "This is the API documentation for the Microservice Challenges application.";
    String applicationVersion = "1.0.0";

    @Bean
    public OpenAPI microserviceChallengesOpenApi() {
        var openApi = new OpenAPI()
                .info(new Info()
                        .title(this.applicationName)
                        .description(this.applicationDescription)
                        .version(this.applicationVersion))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

        return openApi;
    }
}
