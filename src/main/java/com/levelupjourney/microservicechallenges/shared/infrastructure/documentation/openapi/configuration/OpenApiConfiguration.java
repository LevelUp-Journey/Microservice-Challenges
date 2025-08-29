package com.levelupjourney.microservicechallenges.shared.infrastructure.documentation.openapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;


@Configuration
public class OpenApiConfiguration {

    String applicationName = "Microservice Challenges API";
    String applicationDescription = "This is the API documentation for the Microservice Challenges application.";
    String applicationVersion = "1.0.0";

    @Bean
    public OpenAPI microserviceChallengesOpenApi() {
        var openApi = new OpenAPI();
        openApi.info(new io.swagger.v3.oas.models.info.Info()
                .title(this.applicationName)
                .description(this.applicationDescription)
                .version(this.applicationVersion)
                .license(new io.swagger.v3.oas.models.info.License().name("Apache 2.0")
                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new io.swagger.v3.oas.models.ExternalDocumentation()
                        .description("Microservice Challenges Documentation")
                        .url("https://levelupjourney.com/docs/microservice-challenges"));

        return openApi;
    }
}
