package com.levelupjourney.microservicechallenges.challenge.domain.model.commands;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;

public record CreateChallengeVersionCommand(
        ChallengeId challengeId,
        String versionNotes
) {
}
