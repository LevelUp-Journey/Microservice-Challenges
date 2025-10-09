package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;


import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

public record GetChallengeByIdQuery(
        ChallengeId challengeId
) {
}
