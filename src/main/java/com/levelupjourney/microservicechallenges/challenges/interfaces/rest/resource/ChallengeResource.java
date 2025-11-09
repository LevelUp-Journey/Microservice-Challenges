package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;
import java.util.UUID;

public record ChallengeResource(
    String id,
    String teacherId,
    String name,
    String description,
    Integer experiencePoints,
    String difficulty,
    String status,
    List<String> tags,
    List<StarResource> stars,
    List<UUID> guides,
    Integer maxAttemptsBeforeGuides
) {
}