package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.resources;

public record CreateChallengeVersionResource(
        String language,
        String defaultStudentCode
) {
}
