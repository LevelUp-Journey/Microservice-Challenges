package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;

public record PublishChallengeCommand(
        ChallengeId challengeId
) {
}
