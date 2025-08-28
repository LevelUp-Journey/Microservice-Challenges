package com.levelupjourney.microservicechallenges.challenge.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetChallengeTestsByChallengeIdQuery;
import com.levelupjourney.microservicechallenges.challenge.domain.model.queries.GetTestByIdQuery;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.ChallengeId;
import com.levelupjourney.microservicechallenges.shared.domain.model.valueobjects.TestId;

import java.util.UUID;

/**
 * Assembler class to convert REST parameters to Test domain queries
 */
public class TestQueryFromParametersAssembler {

    public static GetTestByIdQuery toGetTestByIdQuery(String testId) {
        return new GetTestByIdQuery(new TestId(UUID.fromString(testId)));
    }

    public static GetChallengeTestsByChallengeIdQuery toGetTestsByChallengeIdQuery(String challengeId) {
        return new GetChallengeTestsByChallengeIdQuery(new ChallengeId(UUID.fromString(challengeId)));
    }
}
