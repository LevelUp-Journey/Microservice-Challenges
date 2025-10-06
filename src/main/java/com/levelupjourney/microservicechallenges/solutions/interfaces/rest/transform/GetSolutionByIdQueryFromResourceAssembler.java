package com.levelupjourney.microservicechallenges.solutions.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutions.domain.model.queries.GetSolutionByIdQuery;
import com.levelupjourney.microservicechallenges.solutions.domain.model.valueobjects.SolutionId;

import java.util.UUID;

public class GetSolutionByIdQueryFromResourceAssembler {

    public static GetSolutionByIdQuery toQueryFromResource(String solutionId) {
        return new GetSolutionByIdQuery(
            new SolutionId(UUID.fromString(solutionId))
        );
    }
}