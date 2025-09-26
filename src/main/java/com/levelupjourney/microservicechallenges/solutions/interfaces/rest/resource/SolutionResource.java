package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

public record SolutionResource(
    String id,
    String challengeId,
    String codeVersionId,
    String studentId,
    Integer attempts,
    String code,
    String lastAttemptAt,
    String status
) {
}