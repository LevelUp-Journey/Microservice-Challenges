package com.levelupjourney.microservicechallenges.shared.interfaces.rest.resources;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.Difficulty;

/**
 * Resource representing Challenge information needed for score calculation.
 * Used by the Solutions context to determine how many points a solution should earn.
 * Includes difficulty level for time-based scoring calculations.
 */
public record ChallengeForScoringResource(
    String challengeId,
    Integer experiencePoints,
    Difficulty difficulty
) {
}
