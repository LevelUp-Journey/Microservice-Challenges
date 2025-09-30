package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a new tag")
public record CreateTagResource(
        @Schema(description = "Tag name", example = "Algorithm", required = true)
        String name,
        
        @Schema(description = "Tag color in hex format", example = "#FF5733")
        String color,
        
        @Schema(description = "Optional icon URL", example = "https://example.com/icon.png")
        String iconUrl
) {
}