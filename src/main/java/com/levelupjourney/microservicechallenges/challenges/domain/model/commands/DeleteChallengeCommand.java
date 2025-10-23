package com.levelupjourney.microservicechallenges.challenges.domain.model.commands;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

public record DeleteChallengeCommand(ChallengeId challengeId) {
}
