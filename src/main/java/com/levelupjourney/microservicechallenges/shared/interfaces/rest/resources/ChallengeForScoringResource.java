package com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources;

/**
 * Resource representing Challenge information needed for score calculation.
 * Used by the Solutions context to determine how many points a solution should earn.
 */
public record ChallengeForScoringResource(
    String challengeId,
    Integer experiencePoints
) {
}
