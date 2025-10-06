package com.levelupjourney.microservicechallenges.challenges.interfaces.rest.resource;

import java.util.List;

public record ChallengeResource(
    String id,
    String teacherId,
    String name,
    String description,
    Integer experiencePoints,
    String status,
    List<String> tags
) {
}