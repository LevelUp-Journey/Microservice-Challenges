package com.levelupjourney.microservicechallenges.solution.domain.model.queries;

import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;

public record GetSolutionsByChallengeIdQuery(ChallengeId challengeId) {
}
