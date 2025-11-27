package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

/**
 * Command to unlike (remove like from) a challenge.
 * 
 * @param challengeId The challenge to unlike
 * @param userId The user who is unliking the challenge
 */
public record UnlikeChallengeCommand(
    ChallengeId challengeId,
    String userId
) {
}
