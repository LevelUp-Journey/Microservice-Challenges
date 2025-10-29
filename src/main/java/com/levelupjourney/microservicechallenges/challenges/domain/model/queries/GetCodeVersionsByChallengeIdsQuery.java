package com.levelupjourney.microservicechallenges.challenges.domain.model.queries;

import com.levelupjourney.microservicechallenges.challenges.domain.model.valueobjects.ChallengeId;

import java.util.List;

/**
 * Query for fetching code versions for multiple challenges in a single operation.
 */
public final class GetCodeVersionsByChallengeIdsQuery {

    private final List<ChallengeId> challengeIds;

    public GetCodeVersionsByChallengeIdsQuery(List<ChallengeId> challengeIds) {
        this.challengeIds = challengeIds;
    }

    public List<ChallengeId> challengeIds() {
        return challengeIds;
    }
}
