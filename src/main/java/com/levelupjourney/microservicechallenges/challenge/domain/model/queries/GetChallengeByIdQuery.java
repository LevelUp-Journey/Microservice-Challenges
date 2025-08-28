package com.levelupjourney.microservicechallenges.challenge.domain.model.queries;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;

public record GetChallengeByIdQuery(ChallengeId challengeId) {
}
