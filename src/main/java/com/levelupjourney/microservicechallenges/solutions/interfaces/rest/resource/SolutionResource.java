package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.resource;

/**
 * Resource representing a Solution with score information.
 * Used for API responses to display solution details including earned points.
 */
public record SolutionResource(
    String id,
    String challengeId,
    String codeVersionId,
    String studentId,
    Integer attempts,
    String code,
    String lastAttemptAt,
    String status,
    Integer pointsEarned,
    Integer maxPoints,
    Double successPercentage
) {
}