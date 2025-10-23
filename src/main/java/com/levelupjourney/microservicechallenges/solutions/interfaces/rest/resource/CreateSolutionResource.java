package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

public record CreateSolutionResource(
    String code
) {
    // challengeId, codeVersionId extracted from URL path
    // studentId extracted from JWT token
}