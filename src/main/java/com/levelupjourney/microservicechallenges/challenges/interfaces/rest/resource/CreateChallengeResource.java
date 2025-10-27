package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;

public record CreateChallengeResource(
    String teacherId,
    String name,
    String description,
    Integer experiencePoints,
    String difficulty,
    List<String> tags  // Tags as strings, e.g., ["#principiante", "#java", "#loops"]
) {
}
