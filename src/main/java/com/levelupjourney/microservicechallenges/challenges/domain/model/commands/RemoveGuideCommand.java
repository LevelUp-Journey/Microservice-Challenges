package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

import java.util.UUID;

public record RemoveGuideCommand(
    ChallengeId challengeId,
    UUID guideId
) {
}

