package com.levelupjourney.microservicechallenges.solution.interfaces.rest.resources;

public record CreateSolutionResource(
        String studentId,
        String challengeId,
        String language,
        String code
) {
}
