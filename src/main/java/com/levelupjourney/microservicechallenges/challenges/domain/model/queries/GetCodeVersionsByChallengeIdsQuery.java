package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

import java.util.List;

/**
 * Query for fetching code versions for multiple challenges in a single operation.
 */
public record GetCodeVersionsByChallengeIdsQuery(List<ChallengeId> challengeIds) {
}
