package com.levelupjourney.microservicechallenges.solutionsreports.interfaces.rest.transform;

import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.queries.GetReportsBySolutionIdQuery;
import com.levelupjourney.microservicechallenges.solutionsreports.domain.model.valueobjects.SolutionId;

import java.util.UUID;

public class GetReportsBySolutionIdQueryFromResourceAssembler {
    
    public static GetReportsBySolutionIdQuery toQueryFromSolutionId(String solutionId) {
        return new GetReportsBySolutionIdQuery(new SolutionId(UUID.fromString(solutionId)));
    }
}