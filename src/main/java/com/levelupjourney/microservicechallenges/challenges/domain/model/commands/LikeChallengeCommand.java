package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

/**
 * Command to like a challenge.
 * 
 * @param challengeId The challenge to like
 * @param userId The user who is liking the challenge
 */
public record LikeChallengeCommand(
    ChallengeId challengeId,
    String userId
) {
}
