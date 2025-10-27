package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

public record CreateSolutionResource(
    String studentId,
    String code
) {
    // challengeId, codeVersionId extracted from URL path
}