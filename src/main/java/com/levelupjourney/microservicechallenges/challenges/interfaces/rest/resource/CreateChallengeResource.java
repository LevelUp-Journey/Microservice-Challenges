package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;
import java.util.UUID;

public record CreateChallengeResource(
    String name,
    String description,
    Integer experiencePoints,
    String difficulty,
    List<String> tags,  // Tags as strings, e.g., ["#principiante", "#java", "#loops"]
    List<UUID> guides,  // Guide IDs, e.g., ["123e4567-e89b-12d3-a456-426614174000"]
    Integer maxAttemptsBeforeGuides  // Max attempts before showing guides
) {
}
