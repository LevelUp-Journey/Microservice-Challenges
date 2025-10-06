package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tag information")
public record TagResource(
        @Schema(description = "Tag ID")
        String id,
        
        @Schema(description = "Tag name", example = "Algorithm")
        String name,
        
        @Schema(description = "Tag color in hex format", example = "#FF5733")
        String color,
        
        @Schema(description = "Icon URL", example = "https://example.com/icon.png")
        String iconUrl
) {
}