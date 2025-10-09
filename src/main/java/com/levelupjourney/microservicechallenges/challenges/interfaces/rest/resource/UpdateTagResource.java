package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update an existing tag")
public record UpdateTagResource(
        @Schema(description = "Tag name", example = "Algorithm")
        String name,
        
        @Schema(description = "Tag color in hex format", example = "#FF5733")
        String color,
        
        @Schema(description = "Icon URL", example = "https://example.com/icon.png")
        String iconUrl
) {
}