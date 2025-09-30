package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.TagId;

public record UnassignTagFromChallengeCommand(
        ChallengeId challengeId,
        TagId tagId
) {
}