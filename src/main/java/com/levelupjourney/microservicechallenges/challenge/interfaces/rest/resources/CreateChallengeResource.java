package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources;

public record CreateChallengeResource(
        String teacherId,
        String title,
        String description
) {
}
