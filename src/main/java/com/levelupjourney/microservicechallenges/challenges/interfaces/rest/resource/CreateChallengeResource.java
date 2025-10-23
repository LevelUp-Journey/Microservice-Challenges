package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;

public record CreateChallengeResource(
    String name, 
    String description, 
    Integer experiencePoints, 
    String difficulty,
    List<String> tags  // Tags as strings, e.g., ["#principiante", "#java", "#loops"]
) {
    // teacherId will be extracted from JWT token
}
